package com.wko.dothings;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
public class DoThingsApplication {

    public static void main(String[] args) {
        SpringApplication.run(DoThingsApplication.class, args);
    }

}
