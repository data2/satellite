package com.data2.satellite.rpc.server.client.client;

import com.data2.satellite.rpc.common.protocol.RpcRequest;
import com.data2.satellite.rpc.server.client.registry.ServiceDiscovery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;
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
        return (T) java.lang.reflect.Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args)
                            throws Throwable {

                        ServerInfo serverInfo = serviceDiscovery.discover();
                        if (Objects.nonNull(serverInfo)) {
                            return new RpcClient(serverInfo).send(prepareRequest(method, args)).getResult();
                        }
                        return null;
                    }
                }
        );
    }

    private RpcRequest prepareRequest(Method method, Object[] args) {
        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);
        return request;
    }
}
