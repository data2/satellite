package com.data2.satellite.rpc.server.registry;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "satellite.registry")
public class ServiceRegistry implements InitializingBean {

    private CountDownLatch latch = new CountDownLatch(1);

    private String address;

    private ZooKeeper zooKeeper;

    public void register(String data) {
        if (!StringUtils.isEmpty(data)) {
            if (zooKeeper != null) {
                AddRootNode(zooKeeper);
                createNode(zooKeeper, data);
            }
        }
    }

    private ZooKeeper connectServer() {
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(address, Constant.ZK_SESSION_TIMEOUT, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getState() == Event.KeeperState.SyncConnected) {
                        latch.countDown();
                    }
                }
            });
            latch.await();
        } catch (IOException e) {
            log.error("", e);
        }
        catch (InterruptedException ex){
            log.error("", ex);
        }
        return zk;
    }

    private void AddRootNode(ZooKeeper zk){
        try {
            Stat s = zk.exists(Constant.ZK_REGISTRY_PATH, false);
            if (s == null) {
                zk.create(Constant.ZK_REGISTRY_PATH, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (KeeperException e) {
            log.error(e.toString());
        } catch (InterruptedException e) {
            log.error(e.toString());
        }
    }

    private void createNode(ZooKeeper zk, String data) {
        try {
            byte[] bytes = data.getBytes();
            String path = zk.create(Constant.ZK_DATA_PATH, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            log.debug("create zookeeper node ({} => {})", path, data);
        } catch (KeeperException e) {
            log.error("", e);
        }
        catch (InterruptedException ex){
            log.error("", ex);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        zooKeeper = connectServer();
    }
}