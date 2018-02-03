package com.wx.dubbo.ch1.provider;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * @author xinquan.huangxq
 */
public class Main {

    public static void main(String[] args) throws IOException {
        new ClassPathXmlApplicationContext("provider.xml");

        System.out.println("服务已经启动...");
        System.in.read();
    }
}
