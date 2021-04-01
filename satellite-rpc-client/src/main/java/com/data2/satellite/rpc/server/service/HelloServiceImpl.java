package com.data2.satellite.rpc.server.service;

import com.data2.satellite.rpc.server.service.bean.Person;
import com.data2.satellite.rpc.server.service.common.RpcService;

@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(String name) {
        return "Hello! " + name;
    }

    @Override
    public String hello(Person person) {
        return "Hello! " + person.getFirstName() + " " + person.getLastName();
    }
}
