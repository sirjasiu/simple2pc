return {
    name = "simple2pc",
    fields = {
        { config = {
            type = "record",
            fields = {
                { urls = { required = true, type = "array", elements = { type = "string" } }
                },
                { http_config = {
                    type = "record",
                    fields = {
                        { connect_timeout = { default = 1000, type = "number" } },
                        { send_timeout = { default = 6000, type = "number" } },
                        { read_timeout = { default = 6000, type = "number" } },
                    }
                } },
            }
        }
        }
    }
}
