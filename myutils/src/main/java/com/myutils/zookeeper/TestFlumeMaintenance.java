package com.myutils.zookeeper;

import org.I0Itec.zkclient.ZkClient;
import org.junit.Test;


public class TestFlumeMaintenance {

    
    private String zkServer = "bigdata1:2181";
    private String topic = "ghwdata";
    private String groupId = "kfk2es";
    String path = "/flume-maintenance" + "/" + topic + "/" + groupId;
    
    @Test
    public void testChangeMaintenance(){
//        ZkClient zkClient = new ZkClient(zkServer);
//        zkClient.writeData(path, "true");
        String[] str = path.split("/flume-maintenance");
        System.out.println(str.length);
        for (String s : str) {
            System.out.println(s);
        }
    }
    
}
