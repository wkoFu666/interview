package com.wko.dothings.common.annotation;


import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogAno {

    // 操作模块
    String logModule() default "";
    // 操作类型
    String logType() default "";
    // 操作信息
    String logDesc() default "";
}
