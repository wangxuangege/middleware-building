package com.wx.zookeeper.myrpc.config;

import lombok.Data;

/**
 * @author xinquan.huangxq
 */
@Data
public class RpcConfig {

    /**
     * 工作线程池大小
     */
    private int minTaskExecutorPoolSize = 8;
    private int maxTaskExecutorPoolSize = 1024;

    /**
     * 调度工作线程池大小
     */
    private int scheduledTaskExecutorPoolSize = 64;

    /**
     * 连接超时关闭时间，秒
     */
    private int idleTime = 600;

    /**
     * TCP连接超时时间
     */
    private int tcpConnectTimeout = 1;

    /**
     * TCP读取超时时间
     */
    private int tcpReadTimeout = 2;
}
