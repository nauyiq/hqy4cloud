package com.hqy.cloud.stream.api;

/**
 * 流操作回调接口
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/4/25
 */
public interface StreamCallback<R> {

    /**
     * 请求成功时.
     * @param data 流请求后的封装结果
     */
    void onSuccess(final R data);

    /**
     * 请求异常时
     * @param cause 异常的原因
     */
    void onFailed(final Throwable cause);

}
