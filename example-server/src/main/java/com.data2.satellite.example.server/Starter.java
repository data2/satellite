package com.data2.satellite.example.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author data2
 * @description
 * @date 2021/4/1 下午5:59
 */
@SpringBootApplication
@ComponentScan("com.data2.satellite")
public class Starter {
    public static void main(Object[] args) {
        SpringApplication.run(Starter.class);
    }
}
