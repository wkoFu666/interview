package com.wko.dothings.wallet.controller;

import com.wko.dothings.common.annotation.LogAno;
import com.wko.dothings.common.annotation.RedisCache;
import com.wko.dothings.common.base.Response;
import com.wko.dothings.utils.IdGenerator;
import com.wko.dothings.wallet.service.LocalService;
import com.wko.dothings.wallet.service.RemoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/test")

public class TestController {

    @Autowired
    private LocalService localService;

    @Autowired
    private RemoteService remoteService;
    @Value("${interface.timeout.limit}")
    private String timeLimit;

//    @LogAno(logModule = "testModule",logType = "QUERY",logDesc = "使用查询接口测试日志注解")
    @GetMapping("/getAllPerson")
    public Response getAllPerson() {
        System.out.println("------------------------------------");
        System.out.println(timeLimit);
        System.out.println("------------------------------------");
        return Response.ok(localService.getAllPerson());
    }

    @RedisCache(nameSpace = "Person",key = "getRemoteAllPerson")
    @LogAno(logModule = "testModule",logType = "QUERY",logDesc = "使用查询接口测试日志注解")
    @GetMapping("/getRemoteAllPerson")
    public Response getRemoteAllPerson() {

        return Response.ok(remoteService.getAllPerson());
    }

    @GetMapping("/id")
    public Response get() throws InterruptedException {
        testIdGenerator();
       return Response.ok();
    }
    @Resource
    IdGenerator idGenerator;

    private ExecutorService es= Executors.newFixedThreadPool(10);
    void testIdGenerator() throws InterruptedException {
        CountDownLatch downLatch=new CountDownLatch(200);
        Runnable task=()->{
            for (int i = 0; i < 100; i++) {
                long orderId = idGenerator.nextId("orderId");
                System.out.println("id="+orderId);
            }
            downLatch.countDown();
        };
        long begin=System.currentTimeMillis();
        for (int i = 0; i < 200; i++) {
            es.submit(task);
        }
        downLatch.await();
        long end=System.currentTimeMillis();
        //花费时常会比实际生成id的时间稍微长一点点。毕竟多了一些打印啊什么的。
        System.out.println("time="+(end-begin));
    }
}
