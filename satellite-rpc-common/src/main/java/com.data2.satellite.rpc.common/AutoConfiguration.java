package com.data2.satellite.rpc.common;

import com.data2.satellite.rpc.common.configuration.ClientConfig;
import com.data2.satellite.rpc.common.configuration.RegistryConfig;
import com.data2.satellite.rpc.common.configuration.ServerConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author data2
 * @description
 * @date 2021/4/2 下午2:59
 */
@EnableConfigurationProperties({ClientConfig.class, RegistryConfig.class, ServerConfig.class})
@Configuration
public class AutoConfiguration {
}
