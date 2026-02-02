package com.hqy.cloud.communication.constants;

import com.hqy.cloud.common.result.Result;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hongqy
 * @date 2026/1/16
 */
@Getter
@AllArgsConstructor
public enum CommunicationResultCode implements Result {

    //  ==================== 参数异常 ====================
    INVALID_PHONE("104001", "手机号码格式不正确"),

    //  ==================== 业务异常 ====================
    FAILED_SEND_SMS("304001", "发送短信失败"),
    NOT_FOUND_SMS_TEMPLATE("304002", "短信模板不存在")

    ;

    private final String code;

    private final String message;

}
