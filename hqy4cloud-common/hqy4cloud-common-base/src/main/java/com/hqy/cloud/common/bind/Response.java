package com.hqy.cloud.common.bind;

import java.io.Serializable;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/24 17:35
 */
public class Response implements Serializable {

    public Response() {
    }

    public Response(boolean result, String message, int code) {
        this.result = result;
        this.message = message;
        this.code = code;
    }

    private boolean result;
    private String message;
    private int code;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
