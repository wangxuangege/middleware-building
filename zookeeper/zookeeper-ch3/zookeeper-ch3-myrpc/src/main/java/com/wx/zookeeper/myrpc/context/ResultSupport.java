package com.wx.zookeeper.myrpc.context;

import com.google.common.collect.Maps;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author xinquan.huangxq
 */
@Data
public final class ResultSupport<T> implements Serializable {

    /**
     * 返回结果状态
     */
    private boolean success = true;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 包装的结果对象，一般success=true时候，model才会填充
     */
    private T model;

    /**
     * 附加结果，因为需要强转，一般不建议使用
     */
    private Map<String, Object> extModels = Maps.newHashMapWithExpectedSize(4);

    /**
     * 创建一个成功对象
     *
     * @param model
     * @param <T>
     * @return
     */
    public static <T> ResultSupport<T> newSuccessResult(T model) {
        ResultSupport<T> result = new ResultSupport<T>();
        result.setSuccess(true);
        result.setModel(model);
        return result;
    }

    /**
     * 创建一个带错误信息的返回对象
     *
     * @param errorMessage
     * @param <T>
     * @return
     */
    public static <T> ResultSupport<T> newErrorResult(String errorMessage) {
        ResultSupport<T> result = new ResultSupport<T>();
        result.setSuccess(false);
        result.setErrorMessage(errorMessage);
        return result;
    }

    /**
     * 创建一个带错误信息和错误码的对象
     *
     * @param errorMessage
     * @param errorCode
     * @param <T>
     * @return
     */
    public static <T> ResultSupport<T> newErrorResult(String errorMessage, String errorCode) {
        ResultSupport<T> result = new ResultSupport<T>();
        result.setSuccess(false);
        result.setErrorMessage(errorMessage);
        result.setErrorCode(errorCode);
        return result;
    }

    /**
     * 获得附加结果
     *
     * @param key
     * @return
     */
    public Object getExtModel(String key) {
        return extModels.get(key);
    }

    /**
     * 设置附加结果
     *
     * @param key
     * @return
     */
    public Object addExtModel(String key, Object value) {
        return extModels.put(key, value);
    }
}