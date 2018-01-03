package com.wx.zookeeper.myrpc.context;

import com.alibaba.fastjson.JSONObject;
import com.wx.zookeeper.myrpc.exception.IExceptionHandler;
import lombok.Data;

/**
 * @author xinquan.huangxq
 */
@Data
public class RpcContext {

    /**
     * 请求报文的json对象
     */
    private JSONObject jsonObjectRequest;

    /**
     * 请求原文
     */
    private String jsonRequest;

    /**
     * 应答原文
     */
    private String jsonResponse;

    /**
     * 协议类型
     */
    private String protocolType;

    /*
     * IP地址
     */
    private String remoteIP;

    /**
     * 服务开始时间
     */
    private long serviceAcceptTime = System.currentTimeMillis();

    /**
     * 处理异常
     */
    private Throwable processException;

    /**
     * 异常处理
     */
    private IExceptionHandler exceptionHandler;
}
