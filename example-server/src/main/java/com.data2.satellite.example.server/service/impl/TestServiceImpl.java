package com.data2.satellite.example.server.service.impl;

import com.data2.satellite.example.server.api.TestService;
import com.data2.satellite.rpc.server.server.anno.RpcService;

/**
 * @author data2
 * @description
 * @date 2021/4/7 下午8:22
 */
@RpcService(TestService.class)
public class TestServiceImpl implements TestService {
    @Override
    public void test() {
        System.out.println("test ok");
    }
}
