package com.data2.satellite.rpc.server.client.route;

import java.util.List;

/**
 * @author data2
 * @description
 * @date 2021/4/2 下午3:43
 */
public interface Router {
    String route(List<String> sourcers);
}
