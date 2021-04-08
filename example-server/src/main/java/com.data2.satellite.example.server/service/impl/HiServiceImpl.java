package com.data2.satellite.example.server.service.impl;

import com.data2.satellite.example.server.api.HiService;
import com.data2.satellite.rpc.server.server.anno.RpcService;

/**
 * @author data2
 * @description
 * @date 2021/4/1 下午5:19
 */
@RpcService(value = HiService.class, version = "1.0.0")
public class HiServiceImpl implements HiService {
    @Override
    public String hi() {
        System.out.println("你好，大兄弟");
        return "你好，大兄弟";
    }
}
