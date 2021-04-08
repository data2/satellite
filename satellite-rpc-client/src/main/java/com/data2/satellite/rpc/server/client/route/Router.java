package com.data2.satellite.rpc.server.client.route;

import com.data2.satellite.rpc.server.client.client.ServerInfo;

import java.util.List;

/**
 * @author data2
 * @description
 * @date 2021/4/2 下午3:43
 */
public interface Router {
    /**
     *
     * @param sourcers
     * @return
     */
    ServerInfo route(List<ServerInfo> sourcers);
}
