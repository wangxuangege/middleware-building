package com.wx.zookeeper.myrpc.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;

/**
 * @author xinquan.huangxq
 */
@Slf4j
public class ResponseEncoder extends MessageToByteEncoder<NettyMsg> {

    @Override
    protected void encode(ChannelHandlerContext ctx, NettyMsg in, ByteBuf out) throws Exception {
        String msg = in.getData();
        try {
            if (StringUtils.isEmpty(msg)) {
                // 心跳包
                out.writeBytes("00000000".getBytes());
                return;
            }

            String body = msg;
            byte[] data = body.getBytes("UTF-8");
            int length = data.length;
            DecimalFormat df = new DecimalFormat("00000000");
            String lengthStr = df.format(length);
            byte[] lengthField = lengthStr.getBytes();
            out.writeBytes(lengthField);
            out.writeBytes(data);
        } catch (Exception e) {
            log.error("", e);
        }
    }

}