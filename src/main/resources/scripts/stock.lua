-- inventory::variant:{variantId}:available:{available}

local stock = redis.call('GET', KEYS[1])

if stock == false then
    return -1
end

local available = tonumber(stock)
local requested = tonumber(ARGV[1])

if available < requested then
    return 0
end

redis.call('DECRBY', KEYS[1], requested)
return 1
