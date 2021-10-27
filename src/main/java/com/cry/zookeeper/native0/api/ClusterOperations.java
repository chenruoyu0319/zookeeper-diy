package com.cry.zookeeper.native0.api;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

@Slf4j
public class ClusterOperations {

    /**
     * 客户端掉线自动重连
     * @throws InterruptedException
     */
    @Test
    public void testReconnect() throws InterruptedException {

        ClusterBase.establishConnect();
        while (true) {
            try {
                Stat stat = new Stat();
                byte[] data = ClusterBase.getZooKeeper().getData("/zookeeper", false, stat);
                log.info("get data : {}", new String(data));

                TimeUnit.SECONDS.sleep(5);
            } catch (Exception e) {
                e.printStackTrace();
                log.info(" 开始重连......");

                while (true) {
                    log.info("zookeeper status :{}", ClusterBase.getZooKeeper().getState().name());
                    if (ClusterBase.getZooKeeper().getState().isConnected()) {
                        break;
                    }
                    TimeUnit.SECONDS.sleep(3);
                }

            }
        }
    }

    @Test
    public void testReadonly() throws InterruptedException {

        ZooKeeper zooKeeper = ClusterBase.getZooKeeper();
        Stat stat = new Stat();
        Watcher watcher = new Watcher() {
            @SneakyThrows
            @Override
            public void process(WatchedEvent event) {
                if ((event.getState() == Event.KeeperState.SyncConnected
                        || event.getState() == Event.KeeperState.ConnectedReadOnly)
                        && event.getType() == Event.EventType.NodeDataChanged) {
                    byte[] data = zooKeeper.getData("/node", this, stat);
                    log.info("数据发生变化: {}", new String(data));
                }
            }
        };
        while (true) {
            try {
                byte[] data = zooKeeper.getData("/node", watcher, stat);
                log.info("session:{},  data from test node :{} ", zooKeeper.getSessionId(), new String(data));
                TimeUnit.SECONDS.sleep(3);
            } catch (Exception e) {
                int count = 0;
                while (zooKeeper.getState() != ZooKeeper.States.CONNECTED
                        && zooKeeper.getState() != ZooKeeper.States.CONNECTEDREADONLY) {


                    TimeUnit.SECONDS.sleep(3);
                    log.info("now state: {} , try: {} times ", zooKeeper.getState().name(), ++count);
                }
            }

        }
    }


}
