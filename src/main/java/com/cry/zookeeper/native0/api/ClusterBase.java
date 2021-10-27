package com.cry.zookeeper.native0.api;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 集群版zk基础配置
 */
@Slf4j
public class ClusterBase  {

    private final static String CLUSTER_CONNECT_STR = "192.168.122.147:2181,192.168.122.147:2182,192.168.122.147:2183,192.168.122.147:2184";

    private static final int CLUSTER_SESSION_TIMEOUT = 60 * 1000;


    private static ZooKeeper zooKeeper = null;

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    private static Watcher watcher = new Watcher() {
        @Override
        public void process(WatchedEvent event) {
            if (event.getState() == Event.KeeperState.SyncConnected
                    && event.getType() == Event.EventType.None) {
                countDownLatch.countDown();
                log.info("连接建立");
            }
        }
    };

    public static ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    protected static String getConnectStr() {
        return CLUSTER_CONNECT_STR;
    }

    protected static int getSessionTimeout() {
        return CLUSTER_SESSION_TIMEOUT;
    }

    public static void establishConnect() {
        try {
            log.info(" start to connect to zookeeper server: {}", getConnectStr());
            zooKeeper = new ZooKeeper(getConnectStr(), getSessionTimeout(), watcher);
            log.info(" 连接中...");
            countDownLatch.await();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        establishConnect();
        TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
    }
}
