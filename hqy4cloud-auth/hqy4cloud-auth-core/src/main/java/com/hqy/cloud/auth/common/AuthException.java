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
        super(code);
    }

    public AuthException(String message, int code) {
        super(message, code);
    }

    public AuthException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
