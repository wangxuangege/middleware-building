package com.wx.dubbo.ch1.provider;

import com.wx.dubbo.ch1.api.TestService;

/**
 * @author xinquan.huangxq
 */
public class TestServiceImpl implements TestService {

    public String sayHello(String name) {
        System.out.println("hello," + name);
        return "hello," + name;
    }
}
