package com.wx.zookeeper.myrpc.netty;

import com.wx.zookeeper.myrpc.context.RpcContext;
import com.wx.zookeeper.myrpc.exception.AbstractExceptionHandler;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xinquan.huangxq
 */
@Slf4j
public class NettyExceptionHandler extends AbstractExceptionHandler {

    private Channel channel;

    public NettyExceptionHandler(Channel channel) {
        this.channel = channel;
    }

    @Override
    protected void response(RpcContext context) {
        if (channel != null) {
            String msg = context.getJsonResponse();

            if (channel.isActive()) {
                channel.writeAndFlush(new NettyMsg(context, msg));
            }
            log.info("完成请求， 耗时：{}", System.currentTimeMillis() - context.getServiceAcceptTime());
        }
    }
}
