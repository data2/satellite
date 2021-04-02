package com.data2.satellite.rpc.server.client;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

public class ServiceDiscovery {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDiscovery.class);
    private CountDownLatch latch = new CountDownLatch(1);
    private volatile List<String> dataList = new ArrayList();
    private String registryAddress;

    public ServiceDiscovery(String registryAddress) {
        this.registryAddress = registryAddress;
        ZooKeeper zk = this.connectServer();
        if (zk != null) {
            this.watchNode(zk);
        }

    }

    public String discover() {
        String data = null;
        int size = this.dataList.size();
        if (size > 0) {
            if (size == 1) {
                data = (String)this.dataList.get(0);
                LOGGER.debug("using only data: {}", data);
            } else {
                data = (String)this.dataList.get(ThreadLocalRandom.current().nextInt(size));
                LOGGER.debug("using random data: {}", data);
            }
        }

        return data;
    }

    private ZooKeeper connectServer() {
        ZooKeeper zk = null;

        try {
            zk = new ZooKeeper(this.registryAddress, 5000, new Watcher() {
                public void process(WatchedEvent event) {
                    if (event.getState() == KeeperState.SyncConnected) {
                        ServiceDiscovery.this.latch.countDown();
                    }

                }
            });
            this.latch.await();
        } catch (InterruptedException | IOException var3) {
            LOGGER.error("", var3);
        }

        return zk;
    }

    private void watchNode(final ZooKeeper zk) {
        try {
            List<String> nodeList = zk.getChildren("/registry", new Watcher() {
                public void process(WatchedEvent event) {
                    if (event.getType() == EventType.NodeChildrenChanged) {
                        ServiceDiscovery.this.watchNode(zk);
                    }

                }
            });
            List<String> dataList = new ArrayList();
            Iterator var4 = nodeList.iterator();

            while(var4.hasNext()) {
                String node = (String)var4.next();
                byte[] bytes = zk.getData("/registry/" + node, false, (Stat)null);
                dataList.add(new String(bytes));
            }

            LOGGER.debug("node data: {}", dataList);
            this.dataList = dataList;
        } catch (InterruptedException | KeeperException var7) {
            LOGGER.error("", var7);
        }

    }

}
