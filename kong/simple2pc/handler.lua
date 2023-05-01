local kong = kong
local kong_meta = require "kong.meta"
local pl_tablex = require "pl.tablex"
local cjson = require('cjson.safe').new()
local http = require 'resty.http'

local EMPTY = pl_tablex.readonly({})

local Simple2pc = {}

Simple2pc.PRIORITY = 2
Simple2pc.VERSION = "0.0.1"

local function interpolate(string, captures, body)
    table = {}
    if captures then
        for h, v in pairs(captures) do
            table[h] = v
        end
    end
    local body_obj
    if type(body) == 'table' then
        body_obj = body
    elseif body then
        body_obj = cjson.decode(body)
    end
    if body_obj then
        for h, v in pairs(body_obj) do
            table[h] = v
        end
    end

    return (string:gsub('($%b{})',
            function(w)
                return table[w:sub(3, -2)] or w
            end))
end

function Simple2pc:access(conf)
    local urls = {}
    local http_config = conf.http_config
    local request = kong.request
    local request_body = request.get_raw_body()
    local request_headers = request.get_headers()
    local uri_captures = (ngx.ctx.router_matches or EMPTY).uri_captures or EMPTY

    -- interpolating urls with captures and body
    for i = 1, #conf.urls do
        urls[i] = interpolate(conf.urls[i], uri_captures, request_body)
    end

    local client = http.new()

    -- calling first phase
    local first_phase_responses = {}
    for i = 1, #urls do
        local url = urls[i]
        client:set_timeouts(http_config.connect_timeout, http_config.send_timeout, http_config.read_timeout)
        local res, err = client:request_uri(url, {
            method = 'POST',
            body = request_body,
            headers = request_headers
        })
        client:set_keepalive(http_config.keepalive_timeout, http_config.keepalive_pool_size)

        if not res or res.status ~= 202 then
            kong.log.err('Invalid response from upstream ', url, ' ', err or ' not accepted')
        end
        first_phase_responses[url] = {
            status = res.status,
            location = res.headers['location'],
            body = res.body or EMPTY }
    end

    -- checking if all ready
    local all_accepted = true
    local not_ok_responses = {}
    for i = 1, #urls do
        local url = urls[i]
        local res = first_phase_responses[url]
        if not res or res.status ~= 202 or not res.location then
            all_accepted = false
            not_ok_responses[url] = { body = res.body, status = res.status }
        end
    end
    if not all_accepted then
        kong.log.err('Not all resources accepted')
    end

    -- calling second phase
    local second_phase_request_body
    if all_accepted then
        second_phase_request_body = "{\"state\": \"committed\"}"
    else
        second_phase_request_body = "{\"state\": \"aborted\"}"
    end
    for i = 1, #urls do
        local url = (first_phase_responses[urls[i]] or EMPTY).location
        if url then
            client:set_timeouts(http_config.connect_timeout, http_config.send_timeout, http_config.read_timeout)

            local res, err = client:request_uri(url, {
                method = 'PATCH',
                body = second_phase_request_body,
                headers = {
                    ["Content-Type"] = "application/json",
                },
            })

            if not res then
                kong.log.err('Could not perform second phase call', url, ' ', err or "not error")
            end
            kong.log.err('Performed ', all_accepted and 'commit' or 'abort', ' action on ', url, ' with result ',
                    res or 'unknown')
        end
    end
    local highest_status = 200
    for _ ,not_ok_response in pairs(not_ok_responses) do
        if highest_status < (not_ok_response.status or 200) then
            highest_status = not_ok_response.status
        end
    end
    return kong.response.exit(all_accepted and 200 or highest_status, all_accepted and "OK" or not_ok_responses)

end

return Simple2pc