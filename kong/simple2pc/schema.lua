return {
  name = "simple-2pc",
  fields = {
      { config = {
          type = "record",
          fields = {
            { urls = { required = true, type = "array", elements = { type = "string" } }
            },
            { http_config = {
                      type = "record",
                      fields = {
                        { connect_timeout = { default = 1000, type = "number" }},
                        { send_timeout = { default = 6000, type = "number" }},
                        { read_timeout = { default = 6000, type = "number" }},
                        { keepalive_timeout = { default = 60, type = "number" }},
                        { keepalive_pool_size = { default = 1000, type = "number" }},
                      }
                    }},
          }
         }
       }
  }
}
