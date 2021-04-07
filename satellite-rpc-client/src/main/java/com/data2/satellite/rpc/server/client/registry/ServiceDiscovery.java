package com.data2.satellite.rpc.server.client.registry;

import com.data2.satellite.rpc.common.configuration.RegistryConfig;
import com.data2.satellite.rpc.server.client.route.RouterFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

@Slf4j
@Component
public class ServiceDiscovery implements InitializingBean {

    @Autowired
    private RouterFactory routerFactory;

    @Autowired
    private RegistryConfig registryConfig;

    private CuratorFramework curatorFramework;
    private volatile List<String> dataList = new ArrayList();

    @Override
    public void afterPropertiesSet() throws Exception {
        connectServer();
        watchNode();

    }

    public String discover() {
        if (!CollectionUtils.isEmpty(dataList)) {
            return routerFactory.getCustomRouter().route(dataList);
        }
        log.error("未发现服务！");
        return null;
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

    private void watchNode() {
        List<String> nodeList = null;
        try {
            TreeCache treeCache = TreeCache.newBuilder(curatorFramework, "/registry").build();
            treeCache.getListenable().addListener(new TreeCacheListener() {
                @Override
                public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
                    if (treeCacheEvent.getType() == TreeCacheEvent.Type.NODE_ADDED ||
                    treeCacheEvent.getType() == TreeCacheEvent.Type.NODE_REMOVED ||
                    treeCacheEvent.getType() == TreeCacheEvent.Type.NODE_UPDATED){
                        reloadData();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reloadData() {
        try {
            dataList = curatorFramework.getChildren().forPath("/registry");
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.debug("load node data: {}", dataList);
    }




}
