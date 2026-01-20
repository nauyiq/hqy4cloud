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

    INVALID_PHONE("INVALID_PHONE", "手机号码格式不正确"),

    ;

    private final String code;

    private final String message;

}
