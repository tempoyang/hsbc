local currentCount = redis.call('INCR', KEYS[1])
if currentCount == 1 then
  redis.call('EXPIRE', KEYS[1], ARGV[1])
end
return currentCount