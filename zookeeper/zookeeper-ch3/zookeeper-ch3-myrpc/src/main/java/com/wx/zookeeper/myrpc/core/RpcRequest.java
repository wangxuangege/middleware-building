package com.wx.zookeeper.myrpc.core;

import lombok.Data;

/**
 * @author xinquan.huangxq
 */
@Data
public class RpcRequest {

    private String requestId;

    private String className;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] parameters;
}
