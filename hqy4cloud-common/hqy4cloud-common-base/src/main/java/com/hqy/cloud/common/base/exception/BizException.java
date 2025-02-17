package com.hqy.cloud.common.base.exception;

import com.hqy.cloud.common.result.Result;
import lombok.Getter;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/24
 */
@Getter
public class BizException extends RuntimeException {

    private final String code;

    public BizException(String code) {
        this.code = code;
    }

    public BizException(String code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(Result result) {
        super(result.getMessage());
        this.code = String.valueOf(result.getCode());
    }

    public BizException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
