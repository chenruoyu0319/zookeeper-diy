package com.cry.zookeeper.curator.cache;

import com.cry.zookeeper.curator.CuratorBaseOperations;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.junit.Test;

import java.util.Map;

/**
 * Path Cache和Node Cache的“合体”，监视路径下的创建、更新、删除事件，并缓存路径下所有孩子结点的数据。
 */
@Slf4j
public class TreeCacheTest extends CuratorBaseOperations {

    public static final String TREE_CACHE="/tree-path";

    @Test
    public void testTreeCache() throws Exception {
        CuratorFramework curatorFramework = getCuratorFramework();
        createIfNeed(TREE_CACHE);
        TreeCache treeCache = new TreeCache(curatorFramework, TREE_CACHE);
        treeCache.getListenable().addListener(new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
                log.info(" tree cache: {}",event);

                Map<String, ChildData> currentChildren = treeCache.getCurrentChildren(TREE_CACHE);
                log.info("currentChildren: {}",currentChildren);
            }
        });
        treeCache.start();
    }
}
