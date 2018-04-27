package com.nettyrpc;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RpcBootstrap {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = 
                new ClassPathXmlApplicationContext("server-spring.xml");
        System.out.println(ctx.isActive() == true);
        ctx.close();
    }
}
