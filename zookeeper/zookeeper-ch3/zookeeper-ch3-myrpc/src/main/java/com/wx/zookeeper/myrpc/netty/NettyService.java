package com.wx.zookeeper.myrpc.netty;

import com.wx.zookeeper.myrpc.config.RpcConfig;
import com.wx.zookeeper.myrpc.task.TaskExecutor;
import io.netty.channel.ChannelHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author xinquan.huangxq
 */
public class NettyService extends NettyServiceTemplate {

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private RpcConfig config;

    @Autowired
    private NettyInvokeService nettyInvokeService;

    @Override
    protected ChannelHandler[] createHandlers() {
        return new ChannelHandler[] {
                new IdleStateHandler(0, 0, config.getIdleTime()),

                new ResponseEncoder(),
                new ResponseLogger(),

                new RequestDecoder(),
                new ContextBuilder(null),
                new RequestLogger(),

                new RequestHandler(nettyInvokeService, taskExecutor)
        };
    }
}
