package com.hqy.cloud.chatgpt.common.exception;

/**
 * OpenAiChatGptException.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/27 11:14
 */
public class OpenAiChatGptException extends RuntimeException {
    private final int code;
    public static final int REQUEST_PARAMS_ERROR = 1;

    public OpenAiChatGptException(int code) {
        this.code = code;
    }

    public OpenAiChatGptException(int code, String message) {
        super(message);
        this.code = code;
    }

    public OpenAiChatGptException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public OpenAiChatGptException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }


}
