package com.hqy.cloud.auth.common;

import com.hqy.cloud.common.base.exception.BizException;
import com.hqy.cloud.common.result.Result;

/**
 * 认证异常
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/11
 */
public class AuthException extends BizException {

    public AuthException(String code) {
        super(code);
    }

    public AuthException(String code, String message) {
        super(code, message);
    }

    public AuthException(Result result) {
        super(result.getCode(), result.getMessage());
    }


    public AuthException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
