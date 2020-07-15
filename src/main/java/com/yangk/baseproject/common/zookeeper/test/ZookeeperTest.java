package com.yangk.baseproject.common.zookeeper.test;

import com.yangk.baseproject.common.zookeeper.WatcherApi;
import com.yangk.baseproject.common.zookeeper.ZookeeperUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Description zookeeper测试类
 * @Author yangkun
 * @Date 2020/6/24
 * @Version 1.0
 * @blame yangkun
 */
@RestController
@RequestMapping("testZookeeper")
public class ZookeeperTest {
    @Resource
    private ZookeeperUtil zookeeperUtil;

    @RequestMapping("setZookeeper")
    public void setZookeeper(String msg) {
        String path = "/" + msg;
        boolean node = zookeeperUtil.createNode(path, "测试" + path);
        System.out.println("=============" + node);
    }

    @RequestMapping("getZookeeper")
    public String getZookeeper(String msg) {
        String path = "/" + msg;
        String value = zookeeperUtil.getData(path, new WatcherApi());
        System.out.println("==================" + value);
        return value;
    }
}
