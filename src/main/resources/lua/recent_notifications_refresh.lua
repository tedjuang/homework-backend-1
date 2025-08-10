-- KEYS[1]: The Redis list key (e.g., "recent_notifications")
-- ARGV: A list of new JSON strings to populate the list

local key = KEYS[1]
local newNotifications = ARGV

-- Atomically clear the old list
redis.call('DEL', key)

-- Atomically re-populate with the new list
for i, jsonStr in ipairs(newNotifications) do
    redis.call('RPUSH', key, jsonStr)
end

return #newNotifications -- Return the count of items added
