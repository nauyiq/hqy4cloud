package com.hqy.cloud.common.base.lang;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/26 10:45
 */
public interface AuthConstants {

    String JWT_PAYLOAD_KEY = "payload";
    String JWT_EXP = "exp";
    String JWT_PREFIX = "bearer ";
    String JWT_UPPERCASE_PREFIX = "Bearer ";
    String JWT_BASIC_PREFIX = "Basic ";
    String CLIENT_ID = "client_id";
    String BIZ_ID = "bizId";
}
