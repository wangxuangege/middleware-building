package com.wx.zookeeper.myrpc.netty;

import com.alibaba.fastjson.JSON;
import com.wx.zookeeper.myrpc.config.RpcConstant;
import com.wx.zookeeper.myrpc.context.RpcContext;
import com.wx.zookeeper.myrpc.core.RpcRequest;
import com.wx.zookeeper.myrpc.core.RpcService;
import com.wx.zookeeper.myrpc.core.ServiceRegistry;
import com.wx.zookeeper.myrpc.exception.RpcException;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xinquan.huangxq
 */
@Slf4j
@Service
public class NettyInvokeService implements ApplicationContextAware, InitializingBean {

    /**
     * 存放接口名与服务对象之间的映射关系
     */
    private Map<String, Object> handlerMap = new HashMap<String, Object>();

    @Autowired
    private ServiceRegistry serviceRegistry;

    /**
     * 请求入口
     *
     * @param context
     * @param channel
     */
    public void invoke(final RpcContext context, final Channel channel) throws RpcException {
        try {
            RpcRequest rpcRequest = JSON.parseObject(context.getJsonRequest(), RpcRequest.class);

            Class<?> claz = Class.forName(rpcRequest.getClassName());
            Object target = handlerMap.get(rpcRequest.getClassName());

            Method method = claz.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
            Object result = method.invoke(target, rpcRequest.getParameters());

            if (channel.isActive()) {
                channel.writeAndFlush(new NettyMsg(null, (String) result));
            }
        } catch (Throwable e) {
            throw new RpcException(RpcConstant.ErrorCode.INTERNAL_ERROR, "执行远程调用发生异常");
        }
    }

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (!CollectionUtils.isEmpty(serviceBeanMap)) {
            for (Object serviceBean : serviceBeanMap.values()) {
                String interfaceName = serviceBean.getClass().getAnnotation(RpcService.class).value().getName();
                handlerMap.put(interfaceName, serviceBean);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (serviceRegistry != null) {
            serviceRegistry.register("localhost:9001"); // 注册服务地址
        }
    }
}
