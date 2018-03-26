package.path = package.path .. ";./extensions/ldbus/?.lua;"

local sensors = require "ldbus_sensors"
local datetime = require "datetime"

-- When the group is SET, the corresponding sync functions will be triggered with one argument: the changed group name as lua string
local redfish_map = {

	{group_name="Update_Sensors", sync_fns={sensors.get_sensors}},
	{group_name="Update_Self_DateTime", sync_fns={datetime.updateSelfDateTime}},

}

return redfish_map
