package com.hqy.cloud.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 内部服务状态码
 * @author hongqy
 * @date 2025/1/24
 */
@Getter
@AllArgsConstructor
public enum BsResultCode implements BsResult {

    SUCCESS("SUCCESS", "成功"),
    FAILED("FAILED", "失败"),
    PARAMS_ERROR("PARAMS_ERROR", "参数错误"),
    PARAM_UNDEFINED("PARAM_UNDEFINED", "参数未定义或空"),
    INSERT_FAILED("INSERT_FAILED", "数据库插入失败"),
    UPDATE_FAILED("UPDATE_FAILED", "数据库更新失败"),
    UNKNOWN_ERROR("UNKNOWN_ERROR", "未知错误"),

    ;

    public final String code;
    public final String message;


}
