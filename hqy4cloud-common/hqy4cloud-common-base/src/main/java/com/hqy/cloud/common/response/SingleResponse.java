package com.hqy.cloud.common.response;

import com.hqy.cloud.common.result.BsResult;
import com.hqy.cloud.common.result.BsResultCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author hongqy
 * @date 2025/1/24
 */
@Getter
@Setter
public class SingleResponse<T> extends BsResponse {

    private T data;

    public SingleResponse(boolean result, String code, String message, T data) {
        super(result, code, message);
        this.data = data;
    }

    public static <T> SingleResponse<T> ok() {
        return new SingleResponse<>(true, BsResultCode.SUCCESS.getCode(), BsResultCode.SUCCESS.getMessage(), null);
    }

    public static <T> SingleResponse<T> ok(T data) {
        return new SingleResponse<>(true, BsResultCode.SUCCESS.getCode(), BsResultCode.SUCCESS.getMessage(), data);
    }

    public static <T> SingleResponse<T> ok(T data, BsResult bsResult) {
        return new SingleResponse<>(true, bsResult.getCode(), bsResult.getMessage(), data);
    }

    public static <T> SingleResponse<T> failed(BsResult bsResult) {
        return new SingleResponse<>(false, bsResult.getCode(), bsResult.getMessage(), null);
    }

    public static <T> SingleResponse<T> failed(T data, BsResult bsResult) {
        return new SingleResponse<>(false, bsResult.getCode(), bsResult.getMessage(), data);
    }

    public static <T> SingleResponse<T> failed(String code, String message) {
        return new SingleResponse<>(false, code, message, null);
    }

    public static <T> SingleResponse<T> failed(String code, String message, T data) {
        return new SingleResponse<>(false, code, message, data);
    }
}
