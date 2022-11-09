package com.wko.dothings.utils;


import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * 全局唯一id生成：每天一个key，便于统计订单量
 * 生成策略：long类型64位：符号位（1位） + 时间戳（31位） + 序列号（32位）组成
 *
 * 也可以稍微加以编码：符号位（1位）+ 版本号（4位）+ 年月日（25位）+ 自增量（26位） +用户id（取最后8位数）
 * 按用户id分表，这样可通过用户ID和订单ID两个维度查询，最多支持1024张表。
 * 年月日：历史数据会归档，通过时间可以确定该订单是否需要去历史库中查询。
 * 版本：以防后期规则发生变化。
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
     * 时间格式：精确到天的统计
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
        //当前时间减去开始时间得到一个递增的时间戳
        long timeStamp = nowSecond - BEGIN_TIMESTAMP;
        String sTime = now.format(DateTimeFormatter.ofPattern(PATTERN));
        //key 按日期自增（每天统计）
        long increment = redisTemplate
                .opsForValue()
                .increment("idIncr:" + keyPrefix + ":" + sTime, DELTA);
        return timeStamp << COUNT_BITS | increment;
    }
}
