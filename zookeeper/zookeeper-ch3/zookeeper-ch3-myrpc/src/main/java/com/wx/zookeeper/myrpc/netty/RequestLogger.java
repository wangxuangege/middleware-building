package com.wx.zookeeper.myrpc.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author xinquan.huangxq
 */
@Slf4j
public class RequestLogger extends MessageToMessageDecoder<NettyMsg> {

    @Override
    protected void decode(ChannelHandlerContext ctx, NettyMsg in, List<Object> out) throws Exception {
        String msg = in.getData();

        if (StringUtils.isEmpty(msg)) {
            log.debug("收到心跳: {}", ctx.channel().remoteAddress());
        } else {
            log.info("收到报文：SockAddress={}, RemoteIP={}\n{}", ctx.channel().remoteAddress(), in.getRemoteIP(), msg);
        }

        out.add(in);
    }
}
