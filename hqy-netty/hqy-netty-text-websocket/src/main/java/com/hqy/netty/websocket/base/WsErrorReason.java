package com.hqy.netty.websocket.base;

import com.hqy.netty.websocket.base.enums.CloseCode;

/**
 * 错误原因
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/8 16:22
 */
public class WsErrorReason {

    private CloseCode code;

    private String reason;

    public WsErrorReason() {
        this.code = CloseCode.NO_STATUS_CODE;
        this.reason = "Just close.";
    }

    public WsErrorReason(CloseCode code, String reason) {
        this.code = code;
        this.reason = reason;
    }

    public CloseCode getCode() {
        return code;
    }

    public void setCode(CloseCode code) {
        this.code = code;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "WsErrorReason{" +
                "code=" + code +
                ", reason='" + reason + '\'' +
                '}';
    }

}
