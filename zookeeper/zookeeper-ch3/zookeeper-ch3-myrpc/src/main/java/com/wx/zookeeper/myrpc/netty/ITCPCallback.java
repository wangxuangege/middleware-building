package com.wx.zookeeper.myrpc.netty;


import com.wx.zookeeper.myrpc.context.RpcContext;
import com.wx.zookeeper.myrpc.exception.RpcException;

public interface ITCPCallback {

	void onSuccess(RpcContext context, byte[] result) throws RpcException;

	void onError(RpcContext context, Throwable e) throws RpcException;
}
