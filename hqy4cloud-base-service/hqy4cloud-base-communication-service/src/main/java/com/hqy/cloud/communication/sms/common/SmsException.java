package com.hqy.cloud.communication.sms.common;

import com.hqy.cloud.common.base.exception.BizException;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/12
 */
public class SmsException extends BizException {

    public SmsException(int code) {
        super(String.valueOf(code));
    }

    public SmsException(int code, String message) {
        super(message, String.valueOf(code));
    }

    public SmsException(int code, String message, Throwable cause) {
        super(String.valueOf(code), message, cause);
    }
}
