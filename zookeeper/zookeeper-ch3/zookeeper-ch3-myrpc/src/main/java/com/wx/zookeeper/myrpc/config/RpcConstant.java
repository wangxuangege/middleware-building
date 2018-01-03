package com.wx.zookeeper.myrpc.config;

/**
 * @author xinquan.huangxq
 */
public class RpcConstant {

    public static final class ErrorCode {
        final public static String TIMEOUT = "TIMEOUT";
        final public static String SYSTEM_BUSY = "SYSTEM_BUSY";
        final public static String NET_ERROR = "NET_ERROR";
        final public static String BAD_REQUEST = "BAD_REQUEST";
        final public static String INTERNAL_ERROR = "INTERNAL_ERROR";
    }

    public static final class ZK {
        final public static int ZK_SESSION_TIMEOUT = 5000;

        final public static String ZK_REGISTRY_PATH = "/registry";

        final public static String ZK_DATA_PATH = ZK_REGISTRY_PATH + "/data";
    }
}
