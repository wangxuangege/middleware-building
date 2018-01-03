package com.wx.zookeeper.myrpc.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author xinquan.huangxq
 */
@Slf4j
public class ResponseLogger extends MessageToMessageEncoder<NettyMsg> {

    @Override
    protected void encode(ChannelHandlerContext ctx, NettyMsg in, List<Object> out) throws Exception {
        String msg = in.getData();
        if (StringUtils.isEmpty(msg)) {
            log.debug("心跳应答: {}", ctx.channel().remoteAddress());
        } else {
            log.info("应答报文: {}\n{}", ctx.channel().remoteAddress(), msg);
        }
        out.add(in);
    }
}