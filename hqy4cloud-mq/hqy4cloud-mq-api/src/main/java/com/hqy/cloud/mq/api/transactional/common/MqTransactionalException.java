package com.hqy.cloud.mq.api.transactional.common;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/7
 */
public class MqTransactionalException extends RuntimeException {

    public static final int INVALID_MESSAGE = 1;
    public static final int SAVING_MESSAGE_ERROR = 2;
    public static final int SEND_MESSAGE_ERROR = 3;


    private final Integer state;

    public MqTransactionalException(Integer state, String message) {
        super(message);
        this.state = state;
    }

    public MqTransactionalException(Integer state, String message, Throwable cause) {
        super(message, cause);
        this.state = state;
    }

    public Integer getState() {
        return state;
    }
}
