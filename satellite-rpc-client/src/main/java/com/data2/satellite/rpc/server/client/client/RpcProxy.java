package com.data2.satellite.rpc.server.client.client;

import com.data2.satellite.rpc.common.protocol.RpcRequest;
import com.data2.satellite.rpc.common.protocol.RpcResponse;
import com.data2.satellite.rpc.server.client.registry.ServiceDiscovery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @author data2
 * @description
 * @date 2021/4/2 下午2:51
 */
@Slf4j
@Component
public class RpcProxy {

    @Autowired
    private ServiceDiscovery serviceDiscovery;

    public <T> T create(Class<T> interfaceClass) {
        return (T)java.lang.reflect.Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args)
                            throws Throwable {
                        RpcRequest request = new RpcRequest(); // 创建并初始化 RPC 请求
                        request.setRequestId(UUID.randomUUID().toString());
                        request.setClassName(method.getDeclaringClass().getName());
                        request.setMethodName(method.getName());
                        request.setParameterTypes(method.getParameterTypes());
                        request.setParameters(args);

                        String serverAddress =  serviceDiscovery.discover();
                        System.out.println("12345678"+serverAddress);
                        if (!StringUtils.isEmpty(serverAddress)) {
                            String[] array = serverAddress.split(":");
                            String host = array[0];
                            int port = Integer.parseInt(array[1]);

                            RpcClient client = new RpcClient(host, port); // 初始化 RPC 客户端
                            RpcResponse response = client.send(request); // 通过 RPC 客户端发送 RPC 请求并获取 RPC 响应

                            return response.getResult();
                        }

                        log.error("未发现服务");
                        return null;

                    }
                }
        );
    }
}
