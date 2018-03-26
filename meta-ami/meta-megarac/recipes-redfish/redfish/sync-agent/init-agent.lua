package.path = package.path .. ";./extensions/ldbus/?.lua;./libs/?;./libs/?.lua;";

local sensors = require("ldbus_sensors")
local CONFIG = require("config")
local redis = require("redis")

local params = {
    scheme = 'unix',
    path = CONFIG.redis_sock,
    -- host = CONFIG.redis_host,
    -- port = CONFIG.redis_port,
    timeout = 0
}

local db = redis.connect(params)

print("Initializing Sensor Map...")
db:del("Redfish:SensorMap:Volt")
db:del("Redfish:SensorMap:Temp")
db:del("Redfish:SensorMap:Fan")
db:set("Redfish:SensorsNotInitialized", true)
repeat
    sensors.init_sensor_list()
    os.execute("sleep 10")
until  db:get("Redfish:SensorsNotInitialized") == nil