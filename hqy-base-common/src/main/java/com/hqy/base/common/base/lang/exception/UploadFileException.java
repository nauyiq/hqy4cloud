package com.hqy.base.common.base.lang.exception;

/**
 * UploadFileException.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/29 11:26
 */
public class UploadFileException extends RuntimeException {

    public UploadFileException(String message) {
        super(message);
    }

    public UploadFileException(Throwable cause) {
        super(cause);
    }

    public UploadFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
