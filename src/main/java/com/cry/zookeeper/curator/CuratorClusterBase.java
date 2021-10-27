package com.cry.zookeeper.curator;


public class CuratorClusterBase extends CuratorStandaloneBase {

    private final static String CLUSTER_CONNECT_STR = "192.168.122.147:2181,192.168.122.147:2182,192.168.122.147:2183,192.168.122.147:2184";

    @Override
    public String getConnectStr() {
        return CLUSTER_CONNECT_STR;
    }
}
