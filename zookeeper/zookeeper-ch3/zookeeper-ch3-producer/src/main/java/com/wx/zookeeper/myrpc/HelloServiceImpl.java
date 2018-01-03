package com.wx.zookeeper.myrpc;

import com.wx.zookeeper.myrpc.core.RpcService;

/**
 * @author xinquan.huangxq
 */
@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {

    public String sayHello(String name) {
        System.out.println("hello," + name);
        return "hello," + name;
    }
}
