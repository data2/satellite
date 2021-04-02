package com.data2.satellite.rpc.server.client.route;

import com.data2.satellite.rpc.common.configuration.ClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author data2
 * @description
 * @date 2021/4/2 下午3:40
 */
@Slf4j
@Component
public class RouterFactory implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    @Autowired
    private ClientConfig clientConfig;

    public Router getCustomRouter(){
        try{
            return (Router) applicationContext.getBean(clientConfig.getRouteName());
        }catch (Exception e){
            log.error("配置的rpc.client.routeName有误，将使用默认随机方式");
            return applicationContext.getBean(RandomRouter.class);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
