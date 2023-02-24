package com.hqy.cloud.common.base.lang.exception;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/2/23 17:07
 */
public class NoAvailableProviderException extends RuntimeException{

    private static final long serialVersionUID = -5285574021908791013L;

    public NoAvailableProviderException() {
        super();
    }

    public NoAvailableProviderException(String message) {
        super(message);
    }

    public NoAvailableProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
