package com.wx.zookeeper.myrpc.task;

import com.wx.zookeeper.myrpc.exception.RpcException;

/**
 * @author xinquan.huangxq
 */
public interface Task {

    void run() throws RpcException;
}
