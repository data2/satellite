package com.nettyrpc.service;

import com.nettyrpc.service.bean.Person;

public interface HelloService {

    String hello(String name);

    String hello(Person person);
}
