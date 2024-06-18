package com.hqy.cloud.common.bind;

import com.hqy.cloud.common.result.Result;
import com.hqy.cloud.common.result.ResultCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/24
 */
@Setter
@Getter
public class R<T> extends Response {

    private T data;

    public static <T> R<T> ok() {
        return setResult(true, ResultCode.SUCCESS);
    }

    public static <T> R<T> ok(T data) {
        return setResult(true,  ResultCode.SUCCESS, data);
    }

    public static <T> R<T> failed() {
        return setResult(false, ResultCode.FAILED);
    }

    public static <T> R<T> failed(String message) {
        return failed(message, ResultCode.FAILED.code);
    }

    public static <T> R<T> failed(String message, int code) {
        return setResult(false, code, message, null);
    }

    public static <T> R<T> failed(Result result) {
        return setResult(false, result);
    }

    public static <T> R<T> setResult(boolean result, Result resultCode) {
        return setResult(result, resultCode, null);
    }

    public static <T> R<T> setResult(boolean result, Result resultCode, T data) {
        return setResult(result, resultCode.getCode(), resultCode.getMessage(), data);
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
