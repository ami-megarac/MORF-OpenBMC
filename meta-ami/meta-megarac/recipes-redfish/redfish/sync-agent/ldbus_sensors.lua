package.path = package.path .. ";./libs/?;./libs/?.lua;"

local redis = require('redis')

local dbus = require('lua-dbus')

local JSON = require('turbo.3rdparty.JSON')

-- provided by sync-agent framework
local CONFIG = require("config")
local ENV  = require("environment")
local utils = require("utils")

local SENSOR_TIMEOUT = 10

local params = {
    scheme = 'unix',
    path = CONFIG.redis_sock,
    -- host = CONFIG.redis_host,
    -- port = CONFIG.redis_port,
    timeout = 0
}

local db = redis.connect(params)

-- function to pretty print a table
local PrettyPrint_callback = function(p)
    io.stdout:write(JSON:encode_pretty(p))
end

-- opts for calling the ObjectMapper's "GetSubTree" method
local ST_opts = {
    bus = "system",
    destination = "xyz.openbmc_project.ObjectMapper",
    path = "/xyz/openbmc_project/object_mapper",
    interface = "xyz.openbmc_project.ObjectMapper",
    args = {"s", "/xyz/openbmc_project/sensors", "i", 0, "as", {}}
}

local sensor_type = function(path)
    if string.match(path, "temperature") then
        return "Temp"
    elseif string.match(path, "voltage") then
        return "Volt"
    elseif string.match(path, "fan_tach") then
        return "Fan"
    else
        return ""
    end
end

-- callback for sensor subtree method, adds map of sensors to redis db
local InitSensorPaths_callback = function(sensor_paths)
    if type(sensor_paths) ~= "table" then
        print(string.format('GetSubTree("/xyz/openbmc_project/sensors") failed on "/xyz/openbmc_project/object_mapper"'))
    else
        db:pipeline(function(pipe)
            for sensor_path, sensor_details in pairs(sensor_paths) do
                local s_type = sensor_type(sensor_path)
                local destination, _interfaces = next(sensor_details)
                pipe:hset("Redfish:SensorMap:"..s_type, sensor_path, destination)
            end
        end)
    end
    db:del("Redfish:SensorsNotInitialized")
    db:del("Redfish:InitSensorsTimeout")
end

-- helpers for calling org.freedesktop.DBus.Properties "GetAll" method on sensors
local prop_opts = function (destination, path)
    return {
        bus = "system",
        destination = destination,
        path = path,
        interface = "org.freedesktop.DBus.Properties",
        args = {"s", ""},
    }
end

local sensors = {}

sensors.init_sensor_list = function()
    dbus.init()

    dbus.call("GetSubTree", InitSensorPaths_callback, ST_opts)

    db:setex("Redfish:InitSensorsTimeout", SENSOR_TIMEOUT, true)
    repeat
        dbus.poll()
    until db:get("Redfish:InitSensorsTimeout") == nil

    dbus.exit()

    return 0
end

-- callbacks for GetAll properties of sensor
local update_volt_sensor = function(sensor_path)
    local s_path = sensor_path
    local callback = function(sensor)
        local sensor_name = string.match(s_path, "/([^/]+)$")
        local prefix = "Redfish:Chassis:".. ENV.ChassisSelf ..":Power:Voltages:"..sensor_name
        -- print(sensor_name)
        -- PrettyPrint_callback(sensor)
        local scale = tonumber(sensor.Scale) or 0
        local factor = 10 ^ scale
        db:mset({
            [prefix ..":Name"] = sensor_name,
            [prefix ..":ReadingVolts"] = sensor.Value and sensor.Value * factor,
            [prefix ..":UpperThresholdNonCritical"] = sensor.WarningHigh and sensor.WarningHigh * factor,
            [prefix ..":UpperThresholdCritical"] = sensor.CriticalHigh and sensor.CriticalHigh * factor,
            -- [prefix ..":UpperThresholdFatal"] = sensor["upper_non_recoverable_threshold"],
            [prefix ..":LowerThresholdNonCritical"] = sensor.WarningLow and sensor.WarningLow * factor,
            [prefix ..":LowerThresholdCritical"] = sensor.CriticalLow and sensor.CriticalLow * factor,
            -- [prefix ..":LowerThresholdFatal"] = sensor["lower_non_recoverable_threshold"],
        })

        local health
        if sensor.CriticalAlarmLow or sensor.CriticalAlarmHigh then
            health = "Critical"
        elseif sensor.WarningAlarmLow or sensor.WarningAlarmHigh then
            health = "Warning"
        else
            health = "OK"
        end

        db:hset(prefix..":Status","State","Enabled")
        db:hset(prefix..":Status","Health",health)

        db:decr("Redfish:GetSensorsSemaphore")
    end
    return callback
end
local update_temp_sensor = function(sensor_path)
    local s_path = sensor_path
    local callback = function(sensor)
        local sensor_name = string.match(s_path, "/([^/]+)$")
        local prefix = "Redfish:Chassis:".. ENV.ChassisSelf ..":Thermal:Temperatures:"..sensor_name
        -- print(sensor_name)
        -- PrettyPrint_callback(sensor)
        local scale = tonumber(sensor.Scale) or 0
        local factor = 10 ^ scale
        db:mset({
            [prefix ..":Name"] = sensor_name,
            [prefix ..":ReadingCelsius"] = sensor.Value and sensor.Value * factor,
            [prefix ..":UpperThresholdNonCritical"] = sensor.WarningHigh and sensor.WarningHigh * factor,
            [prefix ..":UpperThresholdCritical"] = sensor.CriticalHigh and sensor.CriticalHigh * factor,
            -- [prefix ..":UpperThresholdFatal"] = sensor["upper_non_recoverable_threshold"],
            [prefix ..":LowerThresholdNonCritical"] = sensor.WarningLow and sensor.WarningLow * factor,
            [prefix ..":LowerThresholdCritical"] = sensor.CriticalLow and sensor.CriticalLow * factor,
            -- [prefix ..":LowerThresholdFatal"] = sensor["lower_non_recoverable_threshold"],
        })

        local health
        if sensor.CriticalAlarmLow or sensor.CriticalAlarmHigh then
            health = "Critical"
        elseif sensor.WarningAlarmLow or sensor.WarningAlarmHigh then
            health = "Warning"
        else
            health = "OK"
        end

        db:hset(prefix..":Status","State","Enabled")
        db:hset(prefix..":Status","Health",health)

        db:decr("Redfish:GetSensorsSemaphore")
    end
    return callback
end
local update_fan_sensor = function(sensor_path)
    local s_path = sensor_path
    local callback = function(sensor)
        local sensor_name = string.match(s_path, "/([^/]+)$")
        local prefix = "Redfish:Chassis:".. ENV.ChassisSelf ..":Thermal:Fans:"..sensor_name
        -- print(sensor_name)
        -- PrettyPrint_callback(sensor)
        local scale = tonumber(sensor.Scale) or 0
        local factor = 10 ^ scale
        db:mset({
            [prefix ..":FanName"] = sensor_name,
            [prefix ..":ReadingRPM"] = sensor.Value and sensor.Value * factor,
            [prefix ..":UpperThresholdNonCritical"] = sensor.WarningHigh and sensor.WarningHigh * factor,
            [prefix ..":UpperThresholdCritical"] = sensor.CriticalHigh and sensor.CriticalHigh * factor,
            -- [prefix ..":UpperThresholdFatal"] = sensor["upper_non_recoverable_threshold"],
            [prefix ..":LowerThresholdNonCritical"] = sensor.WarningLow and sensor.WarningLow * factor,
            [prefix ..":LowerThresholdCritical"] = sensor.CriticalLow and sensor.CriticalLow * factor,
            -- [prefix ..":LowerThresholdFatal"] = sensor["lower_non_recoverable_threshold"],
        })

        local health
        if sensor.CriticalAlarmLow or sensor.CriticalAlarmHigh then
            health = "Critical"
        elseif sensor.WarningAlarmLow or sensor.WarningAlarmHigh then
            health = "Warning"
        else
            health = "OK"
        end

        db:hset(prefix..":Status","State","Enabled")
        db:hset(prefix..":Status","Health",health)

        db:decr("Redfish:GetSensorsSemaphore")
    end
    return callback
end

sensors.get_sensors = function()

    if db:get("Redfish:SensorsNotInitialized") then
        sensors.init_sensor_list()
    end

    dbus.init()

    local volt_paths = db:hgetall("Redfish:SensorMap:Volt")
    local temp_paths = db:hgetall("Redfish:SensorMap:Temp")
    local fan_paths  = db:hgetall("Redfish:SensorMap:Fan")

    db:set("Redfish:GetSensorsSemaphore", 0)
    for path, dest in pairs(volt_paths) do
        if dbus.call("GetAll", update_volt_sensor(path), prop_opts(dest, path)) then
            db:incr("Redfish:GetSensorsSemaphore")
        end
    end

    for path, dest in pairs(temp_paths) do
        if dbus.call("GetAll", update_temp_sensor(path), prop_opts(dest, path)) then
            db:incr("Redfish:GetSensorsSemaphore")
        end
    end

    for path, dest in pairs(fan_paths) do
        if dbus.call("GetAll", update_fan_sensor(path), prop_opts(dest, path)) then
            db:incr("Redfish:GetSensorsSemaphore")
        end
    end

    db:expire("Redfish:GetSensorsSemaphore", SENSOR_TIMEOUT)
    repeat
        dbus.poll()
    until (tonumber(db:get("Redfish:GetSensorsSemaphore")) or 0) == 0

    db:set("Redfish:Chassis:" .. ENV.ChassisSelf .. ":UpdateSensorsDone", "true")

    dbus.exit()

    return 0
end

return sensors
