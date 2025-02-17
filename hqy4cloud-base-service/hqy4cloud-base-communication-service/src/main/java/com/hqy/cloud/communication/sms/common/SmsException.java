package com.hqy.cloud.communication.sms.common;

import com.hqy.cloud.common.base.exception.BizException;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/12
 */
public class SmsException extends BizException {

    public SmsException(String code) {
        super(code);
    }

    public SmsException(String code, String message) {
        super(code, message);
    }

    public SmsException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
