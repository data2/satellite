package com.data2.satellite.rpc.server.client.registry;

import com.data2.satellite.rpc.common.configuration.RegistryConfig;
import com.data2.satellite.rpc.server.client.route.RouterFactory;
import lombok.extern.slf4j.Slf4j;
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
    private CountDownLatch latch = new CountDownLatch(1);
    private volatile List<String> dataList = new ArrayList();

    @Override
    public void afterPropertiesSet() throws Exception {
        ZooKeeper zk = this.connectServer();
        if (Objects.nonNull(zk)) {
            this.watchNode(zk);
        }
    }

    public String discover() {
        if (!CollectionUtils.isEmpty(dataList)) {
            return routerFactory.getCustomRouter().route(dataList);
        }
        log.error("未发现服务！");
        return null;
    }

    private ZooKeeper connectServer() {
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(registryConfig.getAddress(), 5000, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getState() == KeeperState.SyncConnected) {
                        ServiceDiscovery.this.latch.countDown();
                    }

                }
            });
            this.latch.await();
        } catch (InterruptedException | IOException var3) {
            log.error("", var3);
        }

        return zk;
    }

    private void watchNode(final ZooKeeper zk) {
        try {
            List<String> nodeList = zk.getChildren("/registry", new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getType() == EventType.NodeChildrenChanged) {
                        ServiceDiscovery.this.watchNode(zk);
                    }

                }
            });
            List<String> dataList = new ArrayList();
            Iterator var4 = nodeList.iterator();

            while (var4.hasNext()) {
                String node = (String) var4.next();
                byte[] bytes = zk.getData("/registry/" + node, false, (Stat) null);
                dataList.add(new String(bytes));
            }

            log.debug("node data: {}", dataList);
            this.dataList = dataList;
        } catch (InterruptedException | KeeperException var7) {
            log.error("", var7);
        }

    }

}
