package com.wx.zookeeper.myrpc.core;

import com.alibaba.fastjson.JSON;
import com.wx.zookeeper.myrpc.context.RpcContext;
import com.wx.zookeeper.myrpc.exception.RpcException;
import com.wx.zookeeper.myrpc.netty.ITCPCallback;
import com.wx.zookeeper.myrpc.netty.ShortTCPTransporter;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.concurrent.CountDownLatch;

/**
 * @author xinquan.huangxq
 */
@Slf4j
public class RpcClient {

    private final String host;

    private final int port;

    private final ShortTCPTransporter transporter;

    private CountDownLatch latch = new CountDownLatch(1);

    private volatile RpcResponse response = new RpcResponse();

    public RpcClient(final ShortTCPTransporter transporter, String host, int port) {
        this.transporter = transporter;
        this.host = host;
        this.port = port;
    }

    public RpcResponse send(RpcRequest request) throws InterruptedException, RpcException, UnsupportedEncodingException {
        transporter.send(null, host + ":" + port, generateRequestBytes(request), new ITCPCallback() {
            @Override
            public void onSuccess(RpcContext context, byte[] result) throws RpcException {
                response.setSuccess(true);
                response.setRequestId(request.getRequestId());
                response.setResult(new String(result));

                latch.countDown();
            }

            @Override
            public void onError(RpcContext context, Throwable e) throws RpcException {
                response.setSuccess(false);
                response.setRequestId(request.getRequestId());
                response.setError(e);
                response.setResult(null);

                latch.countDown();
            }
        });
        latch.await();
        return response;
    }

    private byte[] generateRequestBytes(RpcRequest request) throws UnsupportedEncodingException {
        String json = JSON.toJSONString(request);
        byte[] bytes = json.getBytes("UTF-8");
        DecimalFormat df = new DecimalFormat("00000000");
        String lengthStr = df.format(bytes.length);
        return (lengthStr + json).getBytes("UTF-8");
    }
}