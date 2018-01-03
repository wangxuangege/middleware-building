package com.wx.zookeeper.myrpc.netty;

import com.wx.zookeeper.myrpc.config.RpcConstant;
import com.wx.zookeeper.myrpc.exception.RpcException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author xinquan.huangxq
 */
public class ByteDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            return;
        }

        in.markReaderIndex();
        byte[] lengthField = new byte[4];
        in.readBytes(lengthField);
        String lengthStr = new String(lengthField);
        int length = Integer.parseInt(lengthStr);
        if (length > 10 * 1024) {
            throw new RpcException(RpcConstant.ErrorCode.BAD_REQUEST, "报文过长");
        }

        if (length == 0) {
            return;
        }

        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }

        byte[] data = new byte[length];
        in.readBytes(data);

        out.add(data);
    }
}
