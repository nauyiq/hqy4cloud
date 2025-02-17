package com.hqy.cloud.common.response;

import com.hqy.cloud.common.result.Result;
import com.hqy.cloud.common.result.ResultCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author hongqy
 * @date 2025/1/24
 */
@Getter
@Setter
public class SingleResponse<T> extends Response {

    private T data;

    public static <T> SingleResponse<T> ok() {
        return setResult(true, ResultCode.SUCCESS);
    }

    public static <T> SingleResponse<T> ok(T data) {
        return setResult(true, ResultCode.SUCCESS, data);
    }

    public static <T> SingleResponse<T> ok(String code, String message, T data) {
        return setResult(true, code, message, data);
    }

    public static <T> SingleResponse<T> success() {
        return setResult(true, ResultCode.SUCCESS);
    }

    public static <T> SingleResponse<T> success(T data) {
        return setResult(true, ResultCode.SUCCESS, data);
    }

    public static <T> SingleResponse<T> success(String code, String message, T data) {
        return setResult(true, code, message, data);
    }

    public static <T> SingleResponse<T> failed() {
        return setResult(false, ResultCode.FAILED);
    }

    public static <T> SingleResponse<T> failed(String message) {
        return failed(ResultCode.FAILED.code, message);
    }

    public static <T> SingleResponse<T> failed(String code, String message) {
        return setResult(false, code, message, null);
    }

    public static <T> SingleResponse<T> failed(Result result) {
        return setResult(false, result);
    }

    public static <T> SingleResponse<T> setResult(boolean result, Result resultCode) {
        return setResult(result, resultCode, null);
    }

    public static <T> SingleResponse<T> setResult(boolean result, Result resultCode, T data) {
        return setResult(result, resultCode.getCode(), resultCode.getMessage(), data);
    }

    public static <T> SingleResponse<T> setResult(boolean result, String code, String msg, T data) {
        SingleResponse<T> apiResult = new SingleResponse<>();
        apiResult.setResult(result);
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMessage(msg);
        return apiResult;
    }
}
