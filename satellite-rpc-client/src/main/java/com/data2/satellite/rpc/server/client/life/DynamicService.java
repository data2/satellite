package com.data2.satellite.rpc.server.client.life;

import com.data2.satellite.rpc.server.client.registry.ServiceDiscovery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Configuration;

/**
 * @author data2
 * @description
 * @date 2021/4/8 下午2:16
 */
@Slf4j
@Configuration
public class DynamicService implements BeanFactoryPostProcessor {
    @Autowired
    private ServiceDiscovery serviceDiscovery;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
//        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(SingleWorker.class);
//        beanDefinitionBuilder.addPropertyValue("name", inner.name);
//        beanDefinitionBuilder.addPropertyValue("database", inner.database);
//        beanDefinitionBuilder.addPropertyValue("file", inner.file);
//        AbstractBeanDefinition abstractBeanDefinition = beanDefinitionBuilder.getBeanDefinition();
//        abstractBeanDefinition.addQualifier(new AutowireCandidateQualifier(inner.name));
//        abstractBeanDefinition.setScope("prototype");
//        defaultListableBeanFactory.registerBeanDefinition(inner.name, abstractBeanDefinition);
//        log.info("register salmon success : {}", inner.toString());

    }
}
