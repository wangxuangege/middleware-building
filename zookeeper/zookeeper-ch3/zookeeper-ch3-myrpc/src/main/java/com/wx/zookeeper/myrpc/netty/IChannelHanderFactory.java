package com.wx.zookeeper.myrpc.netty;

import io.netty.channel.ChannelHandler;


/**
 * @author xinquan.huangxq
 */
public interface IChannelHanderFactory {

    ChannelHandler getChannelHandler();
}
