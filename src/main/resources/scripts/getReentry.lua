--锁的key
local key = KEYS[1];
--线程唯一标识
local threadId = ARGV[1];
--锁的自动释放时间
local releaseTime = ARGV[2];

--1、判断是否存在锁
--不存在时：
if(redis.call('exists',key) == 0) then
    --不存在，获取锁
    redis.call('hset',key,threadId,'1');
    --设置锁过期时间
    redis.call('expire',key,releaseTime);
    --返回结果
    return 1;
end;
--存在时，判断线程id是否为自己
if(redis.call('hexists',key,threadId) == 1) then
    --是自己则获取锁，重入次数加一
    redis.call('hincrby',key,threadId,'1');
    --重新设置锁过期时间
    redis.call('expire',key,releaseTime);
    return 1;
end;

--走到着说明锁不是自己的，即获取锁失败
return 0;
