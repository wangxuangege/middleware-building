package com.wx.zookeeper.myrpc.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wx.zookeeper.myrpc.context.RpcContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author xinquan.huangxq
 */
@Slf4j
public class ContextBuilder extends MessageToMessageDecoder<NettyMsg> {
    private String protocol;

    public ContextBuilder(String protocol) {
        this.protocol = protocol;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, NettyMsg in, List<Object> out) throws Exception {
        String msg = in.getData();

        JSONObject json;
        if (StringUtils.isBlank(msg)) {
            json = new JSONObject();
        } else {
            try {
                json = JSON.parseObject(msg);
            } catch (Exception e) {
                log.error("JSON解析出错: {}", ctx.channel().remoteAddress());
                json = new JSONObject();
            }
        }

        RpcContext context = new RpcContext();

        if (json.getString("_protocolType") != null) {
            context.setProtocolType(json.getString("_protocolType"));
        } else {
            context.setProtocolType(protocol);
        }
        if (json.getString("_remoteIP") != null) {
            context.setRemoteIP(json.getString("_remoteIP"));
        } else {
            context.setRemoteIP(in.getRemoteIP());
        }
        if (json.getString("_routeAcceptTime") != null) {
            context.setServiceAcceptTime(json.getLong("_routeAcceptTime"));
        } else {
            context.setServiceAcceptTime(System.currentTimeMillis());
        }

        context.setExceptionHandler(new NettyExceptionHandler(ctx.channel()));
        context.setJsonObjectRequest(json);
        context.setJsonRequest(msg);
        in.setContext(context);

        out.add(in);
    }
}