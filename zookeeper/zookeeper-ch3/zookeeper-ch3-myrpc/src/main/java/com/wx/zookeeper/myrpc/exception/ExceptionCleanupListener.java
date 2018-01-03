package com.wx.zookeeper.myrpc.exception;

import com.wx.zookeeper.myrpc.context.RpcContext;

/**
 * @author xinquan.huangxq
 */
public interface ExceptionCleanupListener {

    void cleanup(RpcContext context, Throwable e);
}
