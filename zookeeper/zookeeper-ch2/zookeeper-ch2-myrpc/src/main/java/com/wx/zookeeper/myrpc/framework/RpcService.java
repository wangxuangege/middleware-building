package com.wx.zookeeper.myrpc.framework;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author xinquan.huangxq
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component // 表明可被 Spring 扫描
public @interface RpcService {

    Class<?> value();
}