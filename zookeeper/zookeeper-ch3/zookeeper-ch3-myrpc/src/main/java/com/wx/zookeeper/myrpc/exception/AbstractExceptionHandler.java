package com.wx.zookeeper.myrpc.exception;

import com.alibaba.fastjson.JSON;
import com.wx.zookeeper.myrpc.config.RpcConstant;
import com.wx.zookeeper.myrpc.context.ResultSupport;
import com.wx.zookeeper.myrpc.context.RpcContext;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xinquan.huangxq
 */
@Slf4j
public abstract class AbstractExceptionHandler implements IExceptionHandler {

    private List<ExceptionCleanupListener> listeners = new ArrayList<ExceptionCleanupListener>();

    public AbstractExceptionHandler() {
        super();
    }

    @Override
    public void addCleanupListener(ExceptionCleanupListener l) {
        listeners.add(l);
    }

    @Override
    public boolean removeCleanupListener(ExceptionCleanupListener l) {
        return listeners.remove(l);
    }

    protected void cleanup(RpcContext context, Throwable t) {
        List<ExceptionCleanupListener> copyOfListeners = new ArrayList<>(listeners);
        for (ExceptionCleanupListener l : copyOfListeners) {
            try {
                l.cleanup(context, t);
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    @Override
    public void handleException(RpcContext context, Throwable t) {
        try {
            if (context == null) {
                context = new RpcContext();
            }
            context.setProcessException(t);
            cleanup(context, t);

            ResultSupport<?> resp = null;

            if (t instanceof RpcException) {
                RpcException e = (RpcException) t;
                log.error("业务错误{}: {}", e.getErrCode(), e.getMessage());
                resp = ResultSupport.newErrorResult(e.getMessage(), e.getErrCode());
            } else {
                log.error("异常：", t);
                resp = ResultSupport.newErrorResult("系统内部错误", RpcConstant.ErrorCode.INTERNAL_ERROR);
            }

            String msg = JSON.toJSONString(resp);
            context.setJsonResponse(msg);

            response(context);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Override
    public void handleException(RpcContext context, String errCode, String errInfo) {
        try {
            if (context == null) {
                context = new RpcContext();
            }

            Exception t = new RpcException(errCode, errInfo);
            context.setProcessException(t);

            cleanup(context, t);

            log.error(errInfo);
            ResultSupport<?> resp = ResultSupport.newErrorResult(errInfo, errCode);

            String msg = JSON.toJSONString(resp);
            context.setJsonResponse(msg);

            response(context);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    protected abstract void response(RpcContext context);
}
