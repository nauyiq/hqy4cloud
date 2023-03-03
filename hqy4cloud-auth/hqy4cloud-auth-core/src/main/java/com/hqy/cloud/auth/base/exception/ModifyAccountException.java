package com.hqy.cloud.auth.base.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/13 16:31
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ModifyAccountException extends RuntimeException {
    private static final long serialVersionUID = -2772230436456010539L;

    private int code;

    public ModifyAccountException(String message, int code) {
        super(message);
        this.code = code;
    }

    public ModifyAccountException(Throwable cause, int code) {
        super(cause);
        this.code = code;
    }

    public ModifyAccountException(String message, Throwable cause, int code) {
        super(message, cause);
        this.code = code;
    }
}
