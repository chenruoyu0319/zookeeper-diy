package com.cry.zookeeper.curator.cache;


import com.cry.zookeeper.curator.CuratorStandaloneBase;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.junit.Test;

/**
 * 监视一个结点的创建、更新、删除，并将结点的数据缓存在本地。
 */
@Slf4j
public class NodeCacheTest extends CuratorStandaloneBase {

    public static final String NODE_CACHE="/node-cache";

    @Test
    public void testNodeCacheTest() throws Exception {

        CuratorFramework curatorFramework = getCuratorFramework();

        createIfNeed(NODE_CACHE);
        NodeCache nodeCache = new NodeCache(curatorFramework, NODE_CACHE);
        // 添加监听对象
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            // 如果数据有变化, 则调用该回调方法
            @Override
            public void nodeChanged() throws Exception {
                log.info("{} path nodeChanged: ",NODE_CACHE);
                printNodeData();
            }
        });

        nodeCache.start();
    }


    public void printNodeData() throws Exception {
        CuratorFramework curatorFramework = getCuratorFramework();
        byte[] bytes = curatorFramework.getData().forPath(NODE_CACHE);
        log.info("data: {}",new String(bytes));
    }
}
