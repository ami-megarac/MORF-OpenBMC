-- Update manager time on demand
package.path = package.path .. ";./libs/?;./libs/?.lua;"

local redis = require('redis')
local CONFIG = require("config")

local params = {
	scheme = 'unix',
	path = CONFIG.redis_sock,
	-- host = CONFIG.redis_host,
	-- port = CONFIG.redis_port,
	timeout = 0
}

local time = {}
time.getISO8601SystemTime = function()

	local date_time = os.date("%Y-%m-%dT%H:%M:%S")
	local local_offset = os.date("%z")

	local local_offset_formatted = string.gsub(local_offset, "(%d%d)$", ":%1")
	local date_time_formatted = date_time .. local_offset_formatted

	return date_time_formatted, local_offset_formatted
end

time.updateSelfDateTime = function()

	local db = redis.connect(params)

	local dt_str, offset_str = time.getISO8601SystemTime()

	local ret = db:pipeline(function(pl)
		pl:set("Redfish:Managers:Self:DateTime", dt_str)
		pl:set("Redfish:Managers:Self:DateTimeLocalOffset", offset_str)
	end)

	return ret
end

return time