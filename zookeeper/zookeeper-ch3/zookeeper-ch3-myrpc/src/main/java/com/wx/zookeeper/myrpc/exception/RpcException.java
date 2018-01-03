package com.wx.zookeeper.myrpc.exception;

import com.wx.zookeeper.myrpc.context.RpcContext;
import lombok.Getter;

/**
 * @author xinquan.huangxq
 */
public class RpcException extends Exception {

    @Getter
    private final String errCode;

    @Getter
    private final RpcContext context;

    public RpcException(String code, String info) {
        super(info);
        this.errCode = code;
        this.context = null;
    }

    public RpcException(String code, String info, Throwable t) {
        super(info, t);
        this.errCode = code;
        this.context = null;
    }

    public RpcException(RpcContext context, String code, String info) {
        super(info);
        this.context = context;
        this.errCode = code;
    }

    public RpcException(RpcContext context, String code, String info, Throwable t) {
        super(info, t);
        this.context = context;
        this.errCode = code;
    }

    @Override
    public String toString() {
        String s = getClass().getName();
        String message = getLocalizedMessage();
        return (message != null) ? (s + ": [" + getErrCode() + "]" + message) : s;
    }
}
