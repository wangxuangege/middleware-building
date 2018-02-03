package com.wx.dubbo.ch1.consumer;

import com.wx.dubbo.ch1.api.TestService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * @author xinquan.huangxq
 */
public class Main {

    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("consumer.xml");

        System.out.println("输入任何字符调用远程rpc服务");
        System.in.read();

        TestService testService = context.getBean(TestService.class);
        String sayHello = testService.sayHello("wangxuan");
        System.out.println(sayHello);
    }
}
