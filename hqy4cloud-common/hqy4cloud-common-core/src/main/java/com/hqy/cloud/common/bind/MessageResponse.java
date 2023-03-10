package com.hqy.cloud.common.bind;

import com.hqy.cloud.common.result.ResultCode;

/**
 * @author qy
 * @date 2021-09-14 19:49
 */
public class MessageResponse extends BaseResponse {

    private static final long serialVersionUID = 8029449562864881L;

    /**
     * 业务状态码
     */
    private int code = 0;

    /**
     * 业务响应消息
     */
    private String message;

    public MessageResponse() {
    }

    public MessageResponse(String message) {
        this(false, message);
    }

    public MessageResponse(boolean result, String message) {
        super.setResult(result);
        this.message = message;
        if (!result) {
            code = ResultCode.SYSTEM_ERROR.code;
        }
    }

    public MessageResponse(boolean result, String message, int code) {
        super.setResult(result);
        this.message = message;
        this.code = code;
    }

    public MessageResponse(int code) {
        this(code, null);
    }

    public MessageResponse(int code, String message) {
        this.message = message;
        this.code = code;
        setResult(true);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return getMessage();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
