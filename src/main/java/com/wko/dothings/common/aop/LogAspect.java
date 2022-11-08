package com.wko.dothings.common.aop;


import com.alibaba.fastjson.JSON;
import com.wko.dothings.common.annotation.LogAno;
import com.wko.dothings.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

@Aspect
@Component
@Slf4j
public class LogAspect {

    /**
     * 接口超时限制，超出此限制打印方法执行时间
     */
//    @Value("${interface.timeout.limit}")
//    private Long TIME_LIMIT;

    /**
     * pointcut表达式：com.wko包下所有的ServiceImpl类下的方法
     */
    private static final String POINT_EXECUTION = "execution(* com.wko..*ServiceImpl.*(..))";

    /**
     * PointCut表示这是一个切点，@annotation表示这个切点切到一个注解上，后面带该注解的全类名
     * 切面最主要的就是切点，所有的故事都围绕切点发生
     * logPointCut()代表切点名称
     */
    @Pointcut("@annotation(com.wko.dothings.common.annotation.LogAno)")
    public void logPointCut() {
    }


    /**
     * 环绕通知
     *
     * @param joinPoint，这个参数里面可以获取到所加注解方法的详细信息，包括方法名、入参出参等信息
     * @return
     * @throws Throwable
     */
    @Around("logPointCut()")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        long beginTime = System.currentTimeMillis();
        //执行方法
        Object result = joinPoint.proceed();
        //执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;
        //保存日志
        recordLog(joinPoint, JSON.toJSONString(result), time);
        return result;
    }

    /**
     * 对日志信息进行操作
     *
     * @param joinPoint 切点
     * @param time      方法执行时间
     */
    private void recordLog(ProceedingJoinPoint joinPoint, String result, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogAno logAnnotation = method.getAnnotation(LogAno.class);

        // 日志记录
        log.info("=====================log start================================");
        // 日志基础信息
        log.info("module:{}", logAnnotation.logModule());
        log.info("type:{}", logAnnotation.logType());
        log.info("description:{}", logAnnotation.logDesc());

        // 请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        log.info("request method:{}", className + "." + methodName + "()");

        // 请求的参数
        Object[] args = joinPoint.getArgs();
        String params = JSON.toJSONString(args);
        log.info("params:{}", params);

        // 返回的结果
        log.info("result:{}", result);

        // 获取request 设置IP地址
        HttpServletRequest request = HttpUtils.getHttpServletRequest();
        log.info("ip:{}", HttpUtils.getIpAddress(request));

//        if(time > TIME_LIMIT){
            log.info("execution time : {} ms", time);
//        }
        log.info("=====================log end================================");

        // 这里可以将相关信息提取出来进行一个入库操作
    }


}
