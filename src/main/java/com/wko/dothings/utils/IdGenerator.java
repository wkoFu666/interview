package com.wko.dothings.utils;


import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * 全局唯一id生成
 * long类型64位：符号位（1位） + 时间戳（31位） + 序列号（32位）组成
 * 每天一个key，便于统计订单量
 */
@Component
public class IdGenerator {

    @Resource
    RedisTemplate<String, String> redisTemplate;

    /**
     * 自定义的一个开始时间戳
     */
    private static final long BEGIN_TIMESTAMP = 1667865600L;

    /**
     * 步长：一般情况集群有多台redis那么有几台步长就设置多少
     */
    private final static long DELTA = 1L;

    /**
     * 序列号的一个位数
     */
    private final static int COUNT_BITS = 32;

    /**
     * 时间格式
     */
    private final static String PATTERN = "yyyy:MM:dd";

    /**
     * 获取下一个id值
     *
     * @param keyPrefix key前缀
     * @return id
     */
    public long nextId(String keyPrefix) {
        LocalDateTime now = LocalDateTime.now();
        long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
        long timeStamp = nowSecond - BEGIN_TIMESTAMP;
        String sTime = now.format(DateTimeFormatter.ofPattern(PATTERN));
        Long endTimesTamp = redisTemplate
                .opsForValue()
                .increment("idIncr:" + keyPrefix + ":" + sTime, DELTA);
        return endTimesTamp == null ? 0 : timeStamp << COUNT_BITS | endTimesTamp;
    }
}
