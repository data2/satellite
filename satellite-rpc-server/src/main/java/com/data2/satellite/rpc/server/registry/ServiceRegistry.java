package com.data2.satellite.rpc.server.registry;

import com.data2.satellite.rpc.common.configuration.RegistryConfig;
import com.data2.satellite.rpc.server.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Slf4j
@Component
public class ServiceRegistry implements InitializingBean, DisposableBean {

    @Autowired
    private RegistryConfig registryConfig;

    private String serverNodeFullPath;

    private CuratorFramework curatorFramework;

    public void register(String data) {
        if (!StringUtils.isEmpty(data)) {
            AddRootNode();
            createNode(data);
        }
    }

    private void connectServer() {
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(registryConfig.getAddress())
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .connectionTimeoutMs(15 * 1000)
                .sessionTimeoutMs(60 * 1000)
//                .namespace("")
                .build();
        curatorFramework.start();
    }

    private void AddRootNode() {
        try {
            curatorFramework.create().withProtection().withMode(CreateMode.PERSISTENT).forPath(Constant.ZK_REGISTRY_PATH);
        } catch (Exception e) {
            log.error(NestedExceptionUtils.buildMessage("创建zk服务根节点失败", e));
        }
    }

    private void createNode(String data) {
        try {
            serverNodeFullPath = curatorFramework.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(Constant.ZK_DATA_PATH, data.getBytes());
            log.info("create zookeeper node ({} => {})", serverNodeFullPath, data);
        } catch (Exception e) {
            log.error(NestedExceptionUtils.buildMessage("创建zk服务节点失败", e));
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            connectServer();
        } catch (Exception e) {
            log.error(NestedExceptionUtils.buildMessage("创建zk服务失败", e));
        }
    }

    @Override
    public void destroy() throws Exception {
        log.info("123456789");
        if (Objects.nonNull(curatorFramework)){
            try {
                curatorFramework.delete().guaranteed().forPath(serverNodeFullPath);
                log.info("删除zk服务节点成功，路径{}",serverNodeFullPath);
            }catch (Exception e){
                log.warn("删除zk服务节点失败，等待临时节点超时");
            }
            curatorFramework.close();
        }
    }
}