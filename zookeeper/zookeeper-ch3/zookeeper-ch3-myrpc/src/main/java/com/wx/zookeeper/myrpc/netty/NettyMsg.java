package com.wx.zookeeper.myrpc.netty;

import com.wx.zookeeper.myrpc.context.RpcContext;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author xinquan.huangxq
 */
@Data
public class NettyMsg {

    private RpcContext context;

    private String data;

    private String mac;

    private String remoteIP;

    public NettyMsg(RpcContext context) {
        this(context, null);
    }

    public NettyMsg(RpcContext context, String data) {
        this(context, data, null);
    }

    public NettyMsg(RpcContext context, String data, String mac) {
        this(context, data, mac, null);
    }

    public NettyMsg(RpcContext context, String data, String mac, String remoteIP) {
        this.context = context;
        this.data = data;
        this.mac = mac;
        this.remoteIP = remoteIP;
    }

    public boolean isHeartBeat() {
        return StringUtils.isEmpty(data);
    }
}
