package com.hqy.base.common.exception;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/6 17:25
 */
public class MessageMqException  extends RuntimeException {

    public MessageMqException(String message) {
        super(message);
    }

    public MessageMqException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageMqException(Throwable cause) {
        super(cause);
    }
}
