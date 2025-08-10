-- KEYS[1]: The Redis list key (e.g., "recent_notifications")
-- ARGV[1]: The ID of the notification to update
-- ARGV[2]: The new JSON string of the updated notification

local key = KEYS[1]
local notificationId = ARGV[1]
local newNotificationJson = ARGV[2]

local list = redis.call('LRANGE', key, 0, -1)
local found = false
local newList = {}

for i, jsonStr in ipairs(list) do
    local success, obj = pcall(cjson.decode, jsonStr)
    if success and obj and obj.id == tonumber(notificationId) then
        -- Found the item, replace it with the new JSON
        table.insert(newList, newNotificationJson)
        found = true
    else
        table.insert(newList, jsonStr)
    end
end

if found then
    -- Atomically clear the old list and re-populate with the new list
    redis.call('DEL', key)
    for i, jsonStr in ipairs(newList) do
        redis.call('RPUSH', key, jsonStr) -- Use RPUSH to maintain order as we built newList from head to tail
    end
    return 1                              -- Success
else
    return 0                              -- Not found in the list
end
