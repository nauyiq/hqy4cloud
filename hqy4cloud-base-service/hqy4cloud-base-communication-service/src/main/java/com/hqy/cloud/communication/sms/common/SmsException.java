package com.hqy.cloud.communication.sms.common;

import com.hqy.cloud.common.base.exception.BizException;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/12
 */
public class SmsException extends BizException {

    public SmsException(int code) {
        super(code);
    }

    public SmsException(int code, String message) {
        super(message, code);
    }

    public SmsException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
