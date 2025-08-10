-- KEYS[1]: The Redis list key (e.g., "recent_notifications")
-- ARGV[1]: The JSON string of the new notification to add
-- ARGV[2]: The trim limit (size - 1)

local key = KEYS[1]
local newNotificationJson = ARGV[1]
local trimLimit = tonumber(ARGV[2])

-- Atomically push the new item and trim the list
redis.call('LPUSH', key, newNotificationJson)
redis.call('LTRIM', key, 0, trimLimit)

return 1 -- Success
