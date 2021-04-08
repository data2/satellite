package com.data2.satellite.rpc.server.registry;

import com.data2.satellite.rpc.common.configuration.RegistryConfig;
import com.data2.satellite.rpc.server.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class ServiceRegistry implements InitializingBean, DisposableBean {

    @Autowired
    private RegistryConfig registryConfig;

    private String serverNodeFullPath;

    private CuratorFramework curatorFramework;

    public void registerServerAndService(String data, Map<String, Object> handlerMap) {
        if (!StringUtils.isEmpty(data)) {
            AddRootNode();
            createNodeForServerAndService(data, handlerMap);
        }
    }

    private void connectServer() {
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(registryConfig.getAddress())
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .connectionTimeoutMs(15 * 1000)
                .sessionTimeoutMs(60 * 1000)
                .namespace(registryConfig.getNamespace())
                .build();
        curatorFramework.start();
    }

    private void AddRootNode() {
        try {
            curatorFramework.create().withMode(CreateMode.PERSISTENT).forPath(Constant.ZK_REGISTRY_PATH);
        } catch (Exception e) {
            log.error(NestedExceptionUtils.buildMessage("创建zk服务根节点失败", e));
        }
    }

    private void createNodeForServerAndService(String data, Map<String, Object> handlerMap) {
        try {
            serverNodeFullPath = curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath(Constant.ZK_DATA_PATH, data.getBytes());
            log.info("create zookeeper server node ({} => {})", serverNodeFullPath, data);

            for (String key : handlerMap.keySet()) {
                curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath(serverNodeFullPath + "/" + key, null);
                log.info("create zookeeper service node ({})", key);
            }
        }catch (KeeperException.NodeExistsException e1){
            log.warn("根节点已经存在");
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
    @PreDestroy
    public void destroy() throws Exception {
        log.info("destroy start");
        if (Objects.nonNull(curatorFramework)) {
            try {
                List<String> interfaces = curatorFramework.getChildren().forPath(serverNodeFullPath);
                for (String interfaceStr : interfaces) {
                    curatorFramework.delete().guaranteed().forPath(serverNodeFullPath + "/" + interfaceStr);
                    log.info("delete zookeeper service node success，interface{}", interfaceStr);
                }
                curatorFramework.delete().guaranteed().forPath(serverNodeFullPath);
                log.info("delete zookeeper server node success，路径{}", serverNodeFullPath);
            } catch (Exception e) {
                log.warn("删除zk服务节点失败，等待临时节点超时");
            }
            curatorFramework.close();
        }
    }


}