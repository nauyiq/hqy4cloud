package com.hqy.common.bind;

public class MessageResponse extends BaseResponse {

    public static int CODE_REFRESH_FORCE = 50;
    public static int CODE_REFRESH = 51;

    private static final long serialVersionUID = 8029449562864881L;
    
    private int code = 0;
    
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
            code = 400;
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
        if (code >= 100) {
            setResult(false);
        }
    }

    /**
     * @return 出错消息
     */
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
