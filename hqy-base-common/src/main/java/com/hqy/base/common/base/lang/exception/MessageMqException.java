package com.hqy.base.common.base.lang.exception;

import com.hqy.base.common.result.CommonResultCode;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/6 17:25
 */
public class MessageMqException extends RuntimeException {
    private static final long serialVersionUID = -3601156255152951538L;

    public int code;

    public MessageMqException(String message) {
        this(CommonResultCode.SYSTEM_BUSY.code, message);
    }

    public MessageMqException(int code, String message) {
        super(message);
        this.code = code;
    }

    public MessageMqException(String message, Throwable cause) {
        this(CommonResultCode.SYSTEM_BUSY.code, message, cause);
    }

    public MessageMqException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public MessageMqException(Throwable cause) {
        this(CommonResultCode.SYSTEM_BUSY.code, cause);
    }

    public MessageMqException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
