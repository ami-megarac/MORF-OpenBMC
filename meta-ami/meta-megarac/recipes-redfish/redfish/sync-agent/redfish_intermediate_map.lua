-- Redis DB is going to notify each key change as separate update, so we define an intermediate map
-- which will associate a set of keys to a group (similar to that of the C structure). This group will be
-- pushed to a redis set on any key change under the group. Since it is redis set, the group will be inserted
-- only once even for multiple key change under the same group. Finally the redish_group_map will have the map between
-- the group and the ipmi functions. The group set is processed in a separate coroutine which will trigger
-- the corresponding libipmi function that handles all the data (keys) for a group.

-- Group arguments: the group_name field for intermediate map entries can include two types of keywords used to capture
-- from the key that triggered a match. Anything appended to the group_name using a colon (:) as a delimiter will get captured according to
-- the following patterns:
-- * 1. Number: using a number such as ':1', ':5', ':-1', etc. will return the corresponding segment of the key.
-- 				To get the corresponding segment of the key, the key will be split along the delimiter ':' into an array and
--				the segment found at the index provided will replace the capture pattern when group_name is inserted into the
--				redis group set
-- * 2. ResourceName: using the name of Redfish resource or other key segment such as ':Managers', ':Actions', etc. will return the FOLLOWING
--				segment of the key. i.e. you can give a collection name and the instance ID of the resource will be captured. As with the
--				number pattern above, the key will be split along the delimiter ':' into an array, then the array is searched for the resource
--				name provided, and the next array entry is used to substitute into group_name. NOTE: only exact matches will work, so be careful
--				about what name is actually used by MORF Redfish Framework, e.g.: redis key uses NetworkProtocol for Redfish's ManagerNetworkProtocol 
--				resource, ':Manager" will not match Managers, matches are case-sensitive, etc.
-- Argument examples:
-- map entry:
-- 		{redis_key="Redfish:Managers:" .. Env.ManagerSelf .. ":NetworkProtocol:HostName", group_name="network:-2"},
-- ':-2' captures the second to last key segment, so the group name added to group list = "network:NetworkProtocol"

-- map entry:
-- 		{redis_key="Redfish:Managers:" .. Env.ManagerSelf .. ":NetworkProtocol:HostName", group_name="network:Redfish:1"},
-- :1 captures the first key segment "Redfish", while ':Redfish' captures the segment that follows the matched name "Managers"
-- group name added to the group list = "network:Managers:Redfish"

-- NOTE ABOUT GROUP ARGUMENTS: Group arguments should NOT make the group name unique for different keys in the same group. That is, they
-- should only capture patterns from the part of the keys that are the same between group members. If this is not done, multiple entries in
-- the group list will be added for members from the same group, which defeats the point of grouping keys in the first place.
-- For example, take the keys for properties of SerialInterfaces/IPMI-SOL:
-- group_name="PATCH_Serial:SerialInterfaces" is valid and the consistent group name "PATCH_Serial:IPMI-SOL" would be added for each key
-- group_name="PATCH_Serial:-1" is invalid and would clutter the group list with "PATCH_Serial:Parity", "PATCH_Serial:BitRate", etc.

local Env = require("environment")

-- When the redis_key is SET, this group will be inserted to the db
-- Redis key here can have regular expressions
local redfish_intermediate_map = {

	{redis_key="Redfish:Chassis:" .. Env.ChassisSelf .. ":UpdateSensors", group_name="Update_Sensors"},
	{redis_key="GET:Redfish:Managers:Self:UpdateDateTime", group_name="Update_Self_DateTime"},

}

return redfish_intermediate_map
