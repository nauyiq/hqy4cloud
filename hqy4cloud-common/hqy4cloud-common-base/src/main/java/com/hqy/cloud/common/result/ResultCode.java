package com.hqy.cloud.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 接口级别错误码
 * @author qy
 * @date 2021-08-09
 */
@Getter
@AllArgsConstructor
public enum ResultCode implements Result {

    //  ==================== 成功 ====================
    SUCCESS("000000", "成功"),
    DUPLICATE_SUCCESS("000001", "幂等成功"),


    //  ==================== 参数异常 ====================
    PARAMS_ERROR("100001", "参数错误"),
    PARAM_UNDEFINED("100002", "参数未定义或空"),
    DUPLICATED_REQUEST("100003", "重复的请求"),
    ILLEGAL_REQUEST_LIMITED("100004", "不合法的请求"),


    //  ==================== 数据异常 ====================
    INVALID_DATA("200001", "无效的数据"),

    //  ==================== 认证异常 ====================
    INVALID_ACCESS_TOKEN("400001", "令牌不存在或已过期"),
    NOT_PERMISSION("400002", "没有权限访问"),
    SENTINEL_LIMITED_AUTHORITY("400003", "没有权限, 禁止访问"),


    //  ==================== 文件/oss异常 ====================


    // ==================== 外部错误 ====================


    // ==================== 限流熔断错误 ====================
    TOO_MANY_REQUEST("800001", "请求太频繁, 稍后再试"),
    INTERFACE_BUSY_LIMIT("800002", "接口繁忙,稍后再试"),


    //  ==================== 系统异常 ====================
    FAILED("999991", "失败"),
    INSERT_FAILED("999002", "新增数据失败"),
    UPDATE_FAILED("999003", "更新数据失败"),
    SYSTEM_BUSY("999990", "系统繁忙"),
    SYSTEM_INTERVAL_ERROR("999999", "系统内部错误"),

    ;

    public final String code;

    public final String message;


}
