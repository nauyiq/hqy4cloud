package com.hqy.common.exception;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-23 18:03
 */
public class NoAvailableProvidersException extends RuntimeException {

    public NoAvailableProvidersException() {
        super();
    }

    public NoAvailableProvidersException(String message) {
        super(message);
    }

    public NoAvailableProvidersException(String message, Throwable cause) {
        super(message, cause);
    }
}
