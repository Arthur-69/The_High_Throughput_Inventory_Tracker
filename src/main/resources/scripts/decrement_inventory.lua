local key = KEYS[1]
local requestedQty = tonumber(ARGV[1])

local current = tonumber(redis.call('GET', key) or '0')

if current < requestedQty then
    return -1
end

local remaining = redis.call('DECRBY', key, requestedQty)
return remaining