package com.data2.satellite.rpc.server.client.registry;

import com.data2.satellite.rpc.common.configuration.RegistryConfig;
import com.data2.satellite.rpc.server.client.client.ServerInfo;
import com.data2.satellite.rpc.server.client.route.RouterFactory;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;

@Slf4j
@Data
@Component
public class ServiceDiscovery implements InitializingBean {

    @Autowired
    private RouterFactory routerFactory;
    @Autowired
    private RegistryConfig registryConfig;
    private CuratorFramework curatorFramework;
    private volatile List<ServerInfo> dataList = Lists.newArrayList();
    private volatile HashSet<String> interfaceList = new HashSet<>();


    @Override
    public void afterPropertiesSet() throws Exception {
        connectServer();
        watchNode();
    }

    public ServerInfo discover() {
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
                .namespace(registryConfig.getNamespace())
                .build();
        curatorFramework.start();
    }

    private void watchNode() {
        try {
            TreeCache treeCache = TreeCache.newBuilder(curatorFramework, "/registry").build();
            treeCache.getListenable().addListener(new TreeCacheListener() {
                @Override
                public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
                    if (treeCacheEvent.getType() == TreeCacheEvent.Type.NODE_ADDED ||
                            treeCacheEvent.getType() == TreeCacheEvent.Type.NODE_REMOVED ||
                            treeCacheEvent.getType() == TreeCacheEvent.Type.NODE_UPDATED) {
                        reloadData();
                    }
                }
            });
            reloadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reloadData() {
        try {
            List<String> dataListPath = curatorFramework.getChildren().forPath("/registry");
            for (String path : dataListPath) {
                dataList.add(parseServer(new String(curatorFramework.getData().forPath("/registry/" + path))));
                interfaceList.addAll(curatorFramework.getChildren().forPath("/registry/" + path));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("load node data: {}", dataList);
        log.info("load interface node data: {}", interfaceList);
    }

    private ServerInfo parseServer(String s) {
        String[] arr = s.split("\\:");
        return new ServerInfo(arr[0], Integer.parseInt(arr[1]));
    }


}
