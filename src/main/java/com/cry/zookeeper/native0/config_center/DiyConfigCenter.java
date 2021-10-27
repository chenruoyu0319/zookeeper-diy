package com.cry.zookeeper.native0.config_center;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Chen ruoyu
 * @Description:
 * @Date Created in:  2021-10-14 11:56
 * @Modified By:
 */
@Slf4j
public class DiyConfigCenter {

    private final static String CONNECT_STR = "192.168.122.147:2181";

    /**
     * session timeout base property
     */
    private final static Integer SESSION_TIMEOUT = 30 * 1000;

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    private static ZooKeeper zooKeeper = null;

    public static void main(String[] args) throws InterruptedException, IOException, KeeperException {
        /**
         * zk连接初始化是异步操作,用CountDownLatch保证业务代码的同步性
         * this.sendThread.start(); 请求Session连接
         * this.eventThread.start(); 接受服务端事件
         */
        zooKeeper = new ZooKeeper(CONNECT_STR, SESSION_TIMEOUT, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getType() == Watcher.Event.EventType.None
                        && event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                    log.info("连接已建立");
                    countDownLatch.countDown();
                }
            }
        });
        countDownLatch.await();
        createConfigAndWatch();

        TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
    }

    private static String createConfigAndWatch() throws IOException, InterruptedException, KeeperException {
        DiyConfigCenterBean myConfig = new DiyConfigCenterBean();
        myConfig.setKey("anykey");
        myConfig.setName("anyName");

        // 使用jackson实现序列化
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] bytes = objectMapper.writeValueAsBytes(myConfig);

        Watcher watcher = new Watcher() {
            @SneakyThrows
            @Override
            public void process(WatchedEvent event) {
                if (event.getType() == Event.EventType.NodeDataChanged
                        && event.getPath() != null && event.getPath().equals("/myconfig")) {
                    log.info(" PATH:{}发生了数据变化", event.getPath());

                    byte[] data = zooKeeper.getData("/myconfig", this, null);

                    DiyConfigCenterBean newConfig = objectMapper.readValue(new String(data), DiyConfigCenterBean.class);

                    log.info("数据发生变化: {}", newConfig);

                }
            }
        };
        // 同步方式创建
        String res = zooKeeper.create("/myconfig", bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        log.info("zk创建配置文件RES: {}",res);
        byte[] data = zooKeeper.getData("/myconfig", watcher, null);
        DiyConfigCenterBean dataBean = objectMapper.readValue(data, DiyConfigCenterBean.class);
        log.info("原始数据: {}", dataBean);
        return dataBean.toString();
    }

}