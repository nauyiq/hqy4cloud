package com.hqy.cloud.common.bind;

import com.hqy.cloud.common.result.CommonResultCode;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/24 17:39
 */
public class R<T> extends Response {

    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> R<T> ok() {
        return setResult(true, CommonResultCode.SUCCESS);
    }

    public static <T> R<T> ok(T data) {
        return setResult(true,  CommonResultCode.SUCCESS, data);
    }

    public static <T> R<T> failed() {
        return setResult(false, CommonResultCode.FAILED);
    }

    public static <T> R<T> failed(String message) {
        return setResult(false, CommonResultCode.FAILED.code, message, null);
    }

    public static <T> R<T> failed(CommonResultCode result) {
        return setResult(false, result);
    }

    public static <T> R<T> setResult(boolean result, CommonResultCode resultCode) {
        return setResult(result, resultCode, null);
    }

    public static <T> R<T> setResult(boolean result, CommonResultCode resultCode, T data) {
        return setResult(result, resultCode.code, resultCode.message, data);
    }

    public static <T> R<T> setResult(boolean result, int code, String msg, T data) {
        R<T> apiResult = new R<>();
        apiResult.setResult(result);
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMessage(msg);
        return apiResult;
    }

}
