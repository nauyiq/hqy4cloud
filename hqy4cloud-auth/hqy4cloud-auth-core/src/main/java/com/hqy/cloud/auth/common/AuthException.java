package com.hqy.cloud.auth.common;

import com.hqy.cloud.common.base.exception.BizException;

/**
 * 认证异常
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/11
 */
public class AuthException extends BizException {

    public AuthException(int code) {
        super(String.valueOf(code));
    }

    public AuthException(String message, int code) {
        super(message, String.valueOf(code));
    }

    public AuthException(int code, String message, Throwable cause) {
        super(String.valueOf(code), message, cause);
    }
}
