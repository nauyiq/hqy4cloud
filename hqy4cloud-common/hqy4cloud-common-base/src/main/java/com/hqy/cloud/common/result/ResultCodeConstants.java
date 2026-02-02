package com.hqy.cloud.common.result;

/**
 * 响应码常量类
 * @author hongqy
 * @date 2026/1/30
 */
public interface ResultCodeConstants {

    // ==================== 成功码 ====================
    String SUCCESS = "000000";


    // ==================== 错误类型 (T) ====================
    // 参数错误
    String TYPE_PARAM = "1";
    // 数据错误
    String TYPE_DATA = "2";
    // 业务错误
    String TYPE_BUSINESS = "3";
    // 认证错误
    String TYPE_AUTH = "4";
    // 文件/oss 错误
    String TYPE_FILES = "5";
    // 外部错误
    String TYPE_EXTERNAL = "6";

    // 限流熔断错误
    String TYPE_LIMIT = "8";
    // 系统错误
    String TYPE_SYSTEM = "9";

    // ==================== 业务模块 (MM) ====================
    String MODULE_COMMON = "00";
    String MODULE_ACCOUNT = "01";
    String MODULE_GATEWAY = "02";
    String MODULE_COLLECTION = "03";
    String MODULE_COMMUNICATION = "04";
    String MODULE_ID = "05";
    String MODULE_FRAMEWORK = "99";


}
