-- KEYS[1]: The Redis list key (e.g., "recent_notifications")
-- ARGV[1]: The ID of the notification to delete

local key = KEYS[1]
local notificationId = ARGV[1]

local list = redis.call('LRANGE', key, 0, -1)
local deleted = false
local newList = {}

for i, jsonStr in ipairs(list) do
    local success, obj = pcall(cjson.decode, jsonStr)
    if success and obj and obj.id == tonumber(notificationId) then
        -- Found the item, skip it (effectively deleting)
        deleted = true
    else
        table.insert(newList, jsonStr)
    end
end

if deleted then
    -- Atomically clear the old list and re-populate with the new list
    redis.call('DEL', key)
    for i, jsonStr in ipairs(newList) do
        redis.call('RPUSH', key, jsonStr) -- Use RPUSH to maintain order as we built newList from head to tail
    end
    return 1                              -- Success
else
    return 0                              -- Not found in the list
end
