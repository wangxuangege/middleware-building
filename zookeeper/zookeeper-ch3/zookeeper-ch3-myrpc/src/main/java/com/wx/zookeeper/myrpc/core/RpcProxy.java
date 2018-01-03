package com.wx.zookeeper.myrpc.core;

import com.wx.zookeeper.myrpc.config.RpcConfig;
import com.wx.zookeeper.myrpc.config.RpcConstant;
import com.wx.zookeeper.myrpc.exception.RpcException;
import com.wx.zookeeper.myrpc.netty.ByteDecoder;
import com.wx.zookeeper.myrpc.netty.ByteEncoder;
import com.wx.zookeeper.myrpc.netty.IChannelHanderFactory;
import com.wx.zookeeper.myrpc.netty.ShortTCPTransporter;
import com.wx.zookeeper.myrpc.task.TaskExecutor;
import io.netty.channel.ChannelHandler;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @author xinquan.huangxq
 */
@Slf4j
public class RpcProxy {

    private final  ServiceDiscovery serviceDiscovery;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private RpcConfig config;

    private ShortTCPTransporter transporter;

    public RpcProxy(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        RpcRequest request = new RpcRequest(); // 创建并初始化 RPC 请求
                        request.setRequestId(UUID.randomUUID().toString());
                        request.setClassName(method.getDeclaringClass().getName());
                        request.setMethodName(method.getName());
                        request.setParameterTypes(method.getParameterTypes());
                        request.setParameters(args);

                        if (serviceDiscovery == null) {
                            throw new RpcException(RpcConstant.ErrorCode.SYSTEM_BUSY, "系统繁忙，请稍微再试");
                        }

                        String[] array = serviceDiscovery.discover().split(":");
                        RpcClient client = new RpcClient(transporter, array[0], Integer.parseInt(array[1])); // 初始化 RPC 客户端
                        RpcResponse response = client.send(request); // 通过 RPC 客户端发送 RPC 请求并获取 RPC 响应

                        if (response.isSuccess()) {
                            return response.getResult();
                        } else {
                            throw new RuntimeException(response.getError());
                        }
                    }
                }
        );
    }

    @PostConstruct
    public void init() {
        try {
            transporter = new ShortTCPTransporter();
            transporter.setTaskExecutor(taskExecutor);
            transporter.setConnectTimeout(config.getTcpConnectTimeout());
            transporter.setReadTimeout(config.getTcpReadTimeout());
            transporter.setEncoderFactory(new IChannelHanderFactory() {
                public ChannelHandler getChannelHandler() {
                    return new ByteEncoder();
                }
            });
            transporter.setDecoderFactory(new IChannelHanderFactory() {
                public ChannelHandler getChannelHandler() {
                    return new ByteDecoder();
                }
            });
            transporter.init();
        } catch (Throwable e) {
            log.error("", e);
        }
    }
}
