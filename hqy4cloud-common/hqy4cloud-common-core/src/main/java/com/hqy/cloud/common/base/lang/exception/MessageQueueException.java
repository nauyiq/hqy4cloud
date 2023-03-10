package com.hqy.cloud.common.base.lang.exception;

import com.hqy.cloud.common.result.ResultCode;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/6 17:25
 */
public class MessageQueueException extends RuntimeException {
    private static final long serialVersionUID = -3601156255152951538L;

    public int code;

    public MessageQueueException(String message) {
        this(ResultCode.SYSTEM_BUSY.code, message);
    }

    public MessageQueueException(int code, String message) {
        super(message);
        this.code = code;
    }

    public MessageQueueException(String message, Throwable cause) {
        this(ResultCode.SYSTEM_BUSY.code, message, cause);
    }

    public MessageQueueException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public MessageQueueException(Throwable cause) {
        this(ResultCode.SYSTEM_BUSY.code, cause);
    }

    public MessageQueueException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    /**
     * 消息为空
     */
    public static final int EMPTY_MESSAGE_CODE = 1;

    /**
     * 消息参数为空
     */
    public static final int EMPTY_MESSAGE_PARAMS = 2;

    /**
     * 业务执行异常
     */
    public static final int BUSINESS_EXCEPTION = 3;

    /**
     * 发送消息失败
     */
    public static final int FAILED_SEND_MESSAGE = 4;

}
