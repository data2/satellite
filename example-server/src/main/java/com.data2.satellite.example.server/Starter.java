package com.data2.satellite.example.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author data2
 * @description
 * @date 2021/4/1 下午5:59
 */
@SpringBootApplication
@ComponentScan("com.data2.satellite")
@RestController
public class Starter {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Starter.class);

    }

    @GetMapping
    public String test() {
        return "ok";
    }
}
