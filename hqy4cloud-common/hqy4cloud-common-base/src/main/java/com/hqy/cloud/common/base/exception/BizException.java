package com.hqy.cloud.common.base.exception;

import lombok.Getter;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/24
 */
@Getter
public class BizException extends RuntimeException {

    private final int code;

    public BizException(int code) {
        this.code = code;
    }

    public BizException(String message, int code) {
        super(message);
        this.code = code;
    }

    public BizException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
