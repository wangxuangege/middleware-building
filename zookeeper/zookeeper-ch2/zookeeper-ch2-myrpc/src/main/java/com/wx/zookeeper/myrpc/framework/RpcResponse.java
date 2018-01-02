package com.wx.zookeeper.myrpc.framework;

import lombok.Data;

/**
 * @author xinquan.huangxq
 */
@Data
public class RpcResponse {
    private String requestId;
    private Throwable error;
    private Object result;
}
