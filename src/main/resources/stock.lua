-- if (redis.call('hexists', KEYS[1], KEYS[2]) == 1) then
--     local stock = tonumber(redis.call('hget', KEYS[1], KEYS[2]));
--     if (stock > 0) then
--         redis.call('hincrby', KEYS[1], KEYS[2], -1);
--         return stock;
--     end;
--     return -1;
-- end;

if (redis.call('exists', KEYS[1]) == 1) then
    local stock = tonumber(redis.call('get', KEYS[1]));
    if (stock > 0) then
        local stock2 = redis.call('decrby', KEYS[1], ARGV[1]);
        if(stock2 < 0) then
            redis.call('incrby', KEYS[1], ARGV[1]);
            return -1;
        end;
        return stock2;
    end;
    return -1;
end;
return -2;





-- local key = KEYS[1];
-- local threadId = ARGV[1];
-- local releaseTime = ARGV[2];
--
-- if(redis.call('exists', key) == 0) then
--     redis.call('hset', key, threadId, '1');
--     redis.call('expire', key, releaseTime);
--     return 1;
-- end;
--
-- if(redis.call('hexists', key, threadId) == 1) then
--     redis.call('hincrby', key, threadId, '1');
--     redis.call('expire', key, releaseTime);
--     return 1;
-- end;
-- return 0;
--
-- -----------------------------------------
--
--
-- local key = KEYS[1];
-- local threadId = ARGV[1];
--
-- if (redis.call('hexists', key, threadId) == 0) then
--     return nil; --不是自己返回
-- end;
--
-- local count = redis.call('hincrby', key, threadId, -1);
--
-- if (count == 0) then
--     redis.call('del', key);
--     return nil;
-- end;
