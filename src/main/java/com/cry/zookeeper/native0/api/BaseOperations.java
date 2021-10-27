package com.cry.zookeeper.native0.api;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.TimeUnit;

/**
 * 单机版zk基础api
 */
@Slf4j
public class BaseOperations {

    private static String first_node = "/first-node";

    public static void main(String[] args) throws InterruptedException, KeeperException {
//        testCreate();
//        testGetData();
        StandaloneBase.establishConnect();
        asyncTest();
        asyncTest();
        TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
    }

    /**
     * 创建临时节点
     *
     * @throws KeeperException
     * @throws InterruptedException
     */
    public static void testCreate() throws KeeperException, InterruptedException {
        StandaloneBase.establishConnect();
        ZooKeeper zooKeeper = StandaloneBase.getZooKeeper();
        String s = zooKeeper.create(first_node, "first".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        log.info("Create:{}", s);
    }

    /**
     * 获取节点数据并添加监听(注册中心、配置中心)
     */
    public static void testGetData() {
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getPath() != null && event.getPath().equals(first_node)
                        && event.getType() == Event.EventType.NodeDataChanged) {
                    log.info(" PATH: {}  发现变化", first_node);
                    try {
                        byte[] data = StandaloneBase.getZooKeeper().getData(first_node, this, null);
                        log.info(" data: {}", new String(data));
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        try {
            byte[] data = StandaloneBase.getZooKeeper().getData(first_node, watcher, null);  //
            log.info(" data: {}", new String(data));
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改节点数据(乐观锁)
     *
     * @throws KeeperException
     * @throws InterruptedException
     */
    public static void testSetData() throws KeeperException, InterruptedException {
        ZooKeeper zooKeeper = StandaloneBase.getZooKeeper();
        Stat stat = new Stat();
        byte[] data = zooKeeper.getData(first_node, false, stat);
        int version = stat.getVersion();
        zooKeeper.setData(first_node, "third".getBytes(), 0);
    }

    /**
     * 删除节点及节点下数据, 有子目录则删除失败
     *
     * @throws KeeperException
     * @throws InterruptedException
     */
    public static void testDelete() throws KeeperException, InterruptedException {
        // -1 代表匹配所有版本，直接删除
        // 任意大于 -1 的代表可以指定数据版本删除
        StandaloneBase.getZooKeeper().delete("/config", -1);

    }

    /**
     * 异步获取数据
     */
    public static void asyncTest() {
        String userId = "xxx";
        // 异步获取
//        StandaloneBase.getZooKeeper().getData("/test", false, (rc, path, ctx, data, stat) -> {
//            Thread thread = Thread.currentThread();
//
//            log.info(" Thread Name: {},   rc:{}, path:{}, ctx:{}, data:{}, stat:{}", thread.getName(), rc, path, ctx, data, stat);
//        }, "test");
        AsyncCallback.DataCallback dataCallBack = new AsyncCallback.DataCallback() {
            @Override
            public void processResult(int i, String s, Object o, byte[] bytes, Stat stat) {
                int rc = i;
                String path = s;
                Object ctx = o;
                byte[] data = bytes;
                Thread thread = Thread.currentThread();
                log.info(" Thread Name: {},   rc:{}, path:{}, ctx:{}, data:{}, stat:{}", thread.getName(), rc, path, ctx, data, stat);
            }
        };
        StandaloneBase.getZooKeeper().getData("/myconfig", false, dataCallBack, "test");
        log.info(" over .");

    }
}
