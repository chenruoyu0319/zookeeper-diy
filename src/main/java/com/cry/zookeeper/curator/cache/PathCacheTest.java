package com.cry.zookeeper.curator.cache;


import com.cry.zookeeper.curator.CuratorBaseOperations;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.junit.Test;

/**
 * 监视一个路径下child结点的创建、删除以及结点数据的更新。产生的事件会传递给注册的PathChildrenCacheListener。
 */
@Slf4j
public class PathCacheTest extends CuratorBaseOperations {

    public static final String PATH="/path-cache";

    @Test
    public void testPathCache() throws Exception {

        CuratorFramework curatorFramework = getCuratorFramework();

        createIfNeed(PATH);
        PathChildrenCache pathChildrenCache = new PathChildrenCache(curatorFramework, PATH, true);


        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                log.info("event:  {}",event);
            }
        });

        // 如果设置为true则在首次启动时就会缓存节点内容到Cache中
        pathChildrenCache.start(true);
    }
}
