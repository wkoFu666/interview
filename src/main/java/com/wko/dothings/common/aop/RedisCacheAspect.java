package com.wko.dothings.common.aop;


import com.alibaba.fastjson.JSON;
import com.wko.dothings.common.annotation.RedisCache;
import com.wko.dothings.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
@Slf4j
public class RedisCacheAspect {

    @Resource
    private RedisUtils redisUtils;

    @Pointcut(value = "@annotation(com.wko.dothings.common.annotation.RedisCache)")
    public void redisCache() {
    }

    @Around("redisCache()")
    private Object saveCache(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        log.info("<======拦截到redisCache方法:{}.{}======>",
                proceedingJoinPoint.getTarget().getClass().getName(), proceedingJoinPoint.getSignature().getName());

        // 获取切入的方法对象
        // 这个m是代理对象的，没有包含注解
        Method m = ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod();
        // this()返回代理对象，target()返回目标对象，目标对象反射获取的method对象才包含注解
        Method methodWithAnnotations = proceedingJoinPoint.getTarget().getClass().getDeclaredMethod(
                proceedingJoinPoint.getSignature().getName(), m.getParameterTypes());

        Object result;
        // 根据目标方法对象获取注解对象
        RedisCache annotation = methodWithAnnotations.getDeclaredAnnotation(RedisCache.class);

        // 解析key
        String key = parseKey(methodWithAnnotations, proceedingJoinPoint.getArgs(), annotation.key(), annotation.nameSpace());

        // 到redis中获取缓存
        log.info("<====== 通过key：{}从redis中查询 ======>", key);
        String cache = redisUtils.getCache(key);
        if (cache == null) {
            log.info("<====== Redis 中不存在该记录，从数据库查找 ======>");
            // 若不存在，则到数据库中去获取
            result = proceedingJoinPoint.proceed();
            if (result != null) {
                // 从数据库获取后存入redis, 若有指定过期时间，则设置
                long expireTime = annotation.expireTime();
                if (expireTime != -1) {
                    redisUtils.saveCache(key, result, expireTime, TimeUnit.SECONDS);
                } else {
                    redisUtils.saveCache(key, result);
                }
            }
//            else {
//                // 这里可以做一个布隆过滤器的处理
//            }
            return result;
        } else {
            // 如果缓存中存在数据
            return deSerialize(m, cache);
        }
    }


    /**
     * 反序列化
     *
     * @param m     原方法的对应信息
     * @param cache
     * @return
     */
    private Object deSerialize(Method m, String cache) {
        // 原方法的返回数据类型类
        Class<?> returnTypeClass = m.getReturnType();
        log.info("从缓存中获取数据：{}，返回类型为：{}", cache, returnTypeClass);
        Object object = null;
        // 原方法的返回数据类型类
        Type returnType = m.getGenericReturnType();
        // 判断是否是ParameterizedType的实例，即泛型
        if (returnType instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType) returnType;
            Type[] typeArguments = type.getActualTypeArguments();
            for (Type typeArgument : typeArguments) {
                // 如果是泛型则需要将其中每个单独转换
                Class<?> typeArgClass = (Class<?>) typeArgument;
                log.info("<======获取到泛型:{}", typeArgClass.getName());
                object = JSON.parseArray(cache, typeArgClass);
            }
        } else {
            // 不是泛型则直接转换
            object = JSON.parseObject(cache, returnTypeClass);
        }
        return object;
    }

    /**
     * 解析springEL表达式，生成key
     *
     * @param method    原方法
     * @param argValues 输入参数
     * @param key       key
     * @param nameSpace 命名空间
     * @return
     */
    private String parseKey(Method method, Object[] argValues, String key, String nameSpace) {
        // 创建解析器，但是Spring表达式语言我并不是很熟悉，所以这里暂时不考虑使用
//        ExpressionParser parser = new SpelExpressionParser();
//        Expression expression = parser.parseExpression(key);
//        EvaluationContext context = new StandardEvaluationContext();
//
//        // 添加参数
//        DefaultParameterNameDiscoverer discover = new DefaultParameterNameDiscoverer();
//        String[] parameterNames = discover.getParameterNames(method);
//        for (int i = 0; i < parameterNames.length; i++) {
//            context.setVariable(parameterNames[i], argValues[i]);
//        }
//        // 解析
//        return /*method.getName() + ":" +*/ nameSpace + expression.getValue(context).toString();

//        简单点也可以 命名空间+key+方法名+参数的MD5加密
        StringBuilder prefix = new StringBuilder();
        prefix.append(nameSpace).append(".").append(key);
        prefix.append(".").append(method.getName());
        StringBuilder sb = new StringBuilder();
        for (Object obj : argValues) {
            sb.append(obj.toString());
        }
        return prefix.append(DigestUtils.md5DigestAsHex(sb.toString().getBytes())).toString();
    }

}
