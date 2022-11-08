package com.wko.dothings.common.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface RedisCache {

    /**
     * key的前缀
     */
    String nameSpace() default "";

    /**
     * key
     */
    String key() default "";

    /**
     * 设置过期时间，默认30分钟
     */
    long expireTime() default 1800000;

    /**
     * 是否为查询操作，如果为写入数据库的操作，该值需置为 false
     */
    boolean read() default true;
}
