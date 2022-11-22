package com.hqy.base.common.base.lang.exception;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/9 9:33
 */
public class PublishedException extends RuntimeException{

    private static final long serialVersionUID = -913651013107400573L;

    public PublishedException(String message) {
        super(message);
    }

    public PublishedException(String message, Throwable cause) {
        super(message, cause);
    }

    public PublishedException(Throwable cause) {
        super(cause);
    }
}
