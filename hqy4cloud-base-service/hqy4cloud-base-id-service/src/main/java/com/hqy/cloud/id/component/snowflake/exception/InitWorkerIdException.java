package com.hqy.cloud.id.component.snowflake.exception;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/22 14:43
 */
public class InitWorkerIdException extends RuntimeException {


    public InitWorkerIdException(String message) {
        super(message);
    }

    public InitWorkerIdException() {
    }
}
