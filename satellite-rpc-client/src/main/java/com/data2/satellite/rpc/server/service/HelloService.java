package com.data2.satellite.rpc.server.service;

import com.data2.satellite.rpc.server.service.bean.Person;

public interface HelloService {

    String hello(String name);

    String hello(Person person);
}
