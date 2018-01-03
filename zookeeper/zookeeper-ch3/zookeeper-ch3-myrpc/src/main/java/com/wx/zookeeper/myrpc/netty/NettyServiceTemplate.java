package com.wx.zookeeper.myrpc.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author xinquan.huangxq
 */
@Slf4j
public abstract class NettyServiceTemplate {

    private static EventLoopGroup bossGroup = new NioEventLoopGroup();
    private static EventLoopGroup workerGroup = new NioEventLoopGroup();

    @Setter
    private int port;

    @Setter
    private String name;

    protected abstract ChannelHandler[] createHandlers();

    @PostConstruct
    public void start() throws Exception {
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelHandler[] handlers = createHandlers();
                        for (ChannelHandler handler : handlers) {
                            ch.pipeline().addLast(handler);
                        }
                    }
                }).option(ChannelOption.SO_BACKLOG, 128)
                .option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.SO_REUSEADDR, true);

        // Bind and start to accept incoming connections.
        ChannelFuture cf = b.bind(port).await();
        if (!cf.isSuccess()) {
            log.error("无法绑定端口：" + port);
            throw new Exception("无法绑定端口：" + port);
        }

        log.info("服务[{}]启动完毕，监听端口[{}]", name, port);
    }

    @PreDestroy
    public void stop() {
        bossGroup.shutdownGracefully().syncUninterruptibly();
        workerGroup.shutdownGracefully().syncUninterruptibly();
        log.info("服务[{}]关闭。", name);
    }
}
