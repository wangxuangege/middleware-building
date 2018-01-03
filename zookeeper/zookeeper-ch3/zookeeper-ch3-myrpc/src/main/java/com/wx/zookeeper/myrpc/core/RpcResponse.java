package com.wx.zookeeper.myrpc.core;

import lombok.Data;

/**
 * @author xinquan.huangxq
 */
@Data
public class RpcResponse {

    private String requestId;

    private boolean success;

    private Throwable error;

    private Object result;
}
