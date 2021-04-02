package com.data2.satellite.rpc.common.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author data2
 * @description
 * @date 2021/4/2 下午3:03
 */
@Data
@ConfigurationProperties(prefix = "easy.rpc.server")
public class ServerConfig {
    private String server;
}
