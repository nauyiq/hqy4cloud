package com.hqy.cloud.common.base.lang.exception;

/**
 * UpdateDbException.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/17 11:22
 */
public class UpdateDbException extends RuntimeException {

    public UpdateDbException(String message) {
        super(message);
    }

    public UpdateDbException(String message, Throwable cause) {
        super(message, cause);
    }

    public UpdateDbException(Throwable cause) {
        super(cause);
    }
}
