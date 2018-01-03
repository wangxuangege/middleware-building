package com.wx.zookeeper.myrpc.exception;

import com.wx.zookeeper.myrpc.context.RpcContext;

/**
 * @author xinquan.huangxq
 */
public interface IExceptionHandler {

    void handleException(RpcContext context, Throwable t);

    void handleException(RpcContext context, String errCode, String errInfo);

    void addCleanupListener(ExceptionCleanupListener l);

    boolean removeCleanupListener(ExceptionCleanupListener l);
}
