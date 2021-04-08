package com.data2.satellite.rpc.server.client.client;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author data2
 * @description
 * @date 2021/4/8 下午4:21
 */
@Data
@AllArgsConstructor
public class ServerInfo {
    String host;
    Integer port;
}
