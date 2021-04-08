package com.data2.satellite.rpc.server.server;

/**
 * @author data2
 * @description
 * @date 2021/4/8 下午12:34
 */
public interface RpcListener {

    /**
     *
     * @throws InterruptedException
     */
    void listen() throws InterruptedException;
}
