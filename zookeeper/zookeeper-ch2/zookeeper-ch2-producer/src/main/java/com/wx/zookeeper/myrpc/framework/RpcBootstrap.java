package com.wx.zookeeper.myrpc.framework;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author xinquan.huangxq
 */
public class RpcBootstrap {

    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("applicationContext.xml");
    }
}
