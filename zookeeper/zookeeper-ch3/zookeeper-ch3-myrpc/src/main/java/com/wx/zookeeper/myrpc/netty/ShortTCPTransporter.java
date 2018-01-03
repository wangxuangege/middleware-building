package com.wx.zookeeper.myrpc.netty;

import com.wx.zookeeper.myrpc.config.RpcConstant;
import com.wx.zookeeper.myrpc.context.RpcContext;
import com.wx.zookeeper.myrpc.exception.RpcException;
import com.wx.zookeeper.myrpc.task.Task;
import com.wx.zookeeper.myrpc.task.TaskExecutor;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * TCP短连接
 *
 * @author xinquan.huangxq
 */
@Slf4j
public class ShortTCPTransporter {
    static private final AttributeKey<RpcContext> CTX_KEY = AttributeKey.valueOf("PROCESS_CONTEXT");
    static private final AttributeKey<ITCPCallback> CB_KEY = AttributeKey.valueOf("CALLBACK");
    static private final AttributeKey<Long> START_TIME = AttributeKey.valueOf("START_TIME");
    static private final AttributeKey<Boolean> RESPONDED = AttributeKey.valueOf("RESPONDED");

    @Setter
    @Getter
    private TaskExecutor taskExecutor;

    private NioEventLoopGroup bossGroup;
    private Bootstrap bootStrap;

    @Setter
    @Getter
    /**
     * 连接超时时间，单位秒
     */
    private int connectTimeout;

    @Setter
    @Getter
    /**
     * 通讯超时时间，单位秒
     */
    private int readTimeout;

    @Setter
    @Getter
    private IChannelHanderFactory encoderFactory;

    @Setter
    @Getter
    private IChannelHanderFactory decoderFactory;

    /**
     * 初始化
     */
    public void init() {
        bossGroup = new NioEventLoopGroup(4);
        bootStrap = new Bootstrap();
        bootStrap.group(bossGroup);
        bootStrap.channel(NioSocketChannel.class);
        bootStrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootStrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout * 1000);
        bootStrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(
                        new IdleStateHandler(readTimeout, 0, 0),
                        encoderFactory.getChannelHandler(),
                        decoderFactory.getChannelHandler(),
                        new ClientHandler());
            }
        });

    }

    private class ClientHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object in) throws Exception {

            ctx.channel().attr(RESPONDED).set(true);

            final byte[] data = (byte[]) in;

            final RpcContext context = ctx.channel().attr(CTX_KEY).get();
            final ITCPCallback callback = ctx.channel().attr(CB_KEY).get();

            log.info("收到TCP应答：{}", ctx.channel().remoteAddress());

            long startTime = ctx.channel().attr(START_TIME).get();

            taskExecutor.execute(context, new Task() {
                public void run() throws RpcException {
                    log.info("耗时：{}", System.nanoTime() - startTime);
                    callback.onSuccess(context, data);
                }
            });

            ctx.channel().close();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, final Throwable cause) throws Exception {
            final RpcContext context = ctx.channel().attr(CTX_KEY).get();

            ctx.channel().attr(RESPONDED).set(true);

            if (cause instanceof IOException) {
                log.error("通讯异常：{}, {}", cause.getMessage(), ctx.channel().remoteAddress());
            } else {
                log.error("未捕获异常：" + ctx.channel().remoteAddress(), cause);
            }

            final ITCPCallback callback = ctx.channel().attr(CB_KEY).get();

            long startTime = ctx.channel().attr(START_TIME).get();

            taskExecutor.execute(context, new Task() {
                public void run() throws RpcException {
                    log.info("耗时：{}", System.nanoTime() - startTime);
                    callback.onError(context, new RpcException(RpcConstant.ErrorCode.NET_ERROR, cause.getMessage()));
                }
            });

            if (ctx.channel().isActive()) {
                ctx.channel().close();
            }
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                log.info("连接读取超时，强制关闭：{}", ctx.channel().remoteAddress());

                ctx.channel().attr(RESPONDED).set(true);

                final RpcContext context = ctx.channel().attr(CTX_KEY).get();
                final ITCPCallback callback = ctx.channel().attr(CB_KEY).get();
                long startTime = ctx.channel().attr(START_TIME).get();
                taskExecutor.execute(context, new Task() {
                    public void run() throws RpcException {
                        log.info("耗时：{}", System.nanoTime() - startTime);
                        callback.onError(context, new RpcException(RpcConstant.ErrorCode.TIMEOUT, "业务方无应答"));
                    }
                });

                ctx.channel().close();
            }
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            final RpcContext context = ctx.channel().attr(CTX_KEY).get();
            log.info("连接已关闭: {}", ctx.channel().remoteAddress());

            Boolean responded = ctx.channel().attr(RESPONDED).get();
            if (responded == null || responded == false) {
                log.info("网络连接异常关闭");
                final ITCPCallback callback = ctx.channel().attr(CB_KEY).get();
                long startTime = ctx.channel().attr(START_TIME).get();
                taskExecutor.execute(context, new Task() {
                    public void run() throws RpcException {
                        log.info("耗时：{}", System.nanoTime() - startTime);
                        callback.onError(context, new RpcException(RpcConstant.ErrorCode.NET_ERROR, "网络连接异常关闭"));
                    }
                });
            }
        }
    }

    public void send(RpcContext context, String hosts, byte[] data, ITCPCallback callback) throws RpcException {
        // 打乱顺序
        String[] hostArray = hosts.split(",");
        List<String> list = Arrays.asList(hostArray);
        Collections.shuffle(list);
        internalSend(context, list, 0, data, callback);
    }

    private void internalSend(RpcContext context, List<String> hostList, int hostIndex, byte[] data, ITCPCallback callback) throws RpcException {
        String[] hostParts = hostList.get(hostIndex).trim().split(":");
        String host = hostParts[0];
        int port = Integer.parseInt(hostParts[1]);
        InetSocketAddress addr = new InetSocketAddress(host, port);

        long startTime = System.nanoTime();

        log.info("连接: {}", addr);
        bootStrap.connect(addr).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture cf) throws Exception {
                if (cf.isSuccess()) {
                    log.info("成功连接: {}", addr);
                    cf.channel().attr(CTX_KEY).set(context);
                    cf.channel().attr(CB_KEY).set(callback);
                    cf.channel().attr(START_TIME).set(startTime);

                    cf.channel().writeAndFlush(data);
                } else {
                    log.warn("无法连接: {}", addr);
                    if (hostIndex + 1 >= hostList.size()) {
                        taskExecutor.execute(context, new Task() {
                            public void run() throws RpcException {
                                callback.onError(context, new RpcException(RpcConstant.ErrorCode.NET_ERROR, "无法连接业务方"));
                            }
                        });
                    } else {
                        internalSend(context, hostList, hostIndex + 1, data, callback);
                    }
                }
            }
        });
    }

}
