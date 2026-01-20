package com.hqy.cloud.infrastructure.random;

import java.util.concurrent.TimeUnit;

/**
 * RedisRandomCodeServer.
 * Generator Random code.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/14
 */
public interface RandomCodeService {

    int DEFAULT_LENGTH = 6;

    default String randomNumber(int expired, TimeUnit timeUnit, RandomCodeScene scene, String value) {
        return randomNumber(DEFAULT_LENGTH, expired, timeUnit, scene, value);
    }

    default String randomNumber(int length, int expired, TimeUnit timeUnit, RandomCodeScene scene, String value) {
        return randomNumber(length, expired, timeUnit, scene, "default", value);
    }

    String randomNumber(int length, int expired, TimeUnit timeUnit, RandomCodeScene scene, String clientId, String value);

    default String randomStr(int expired, TimeUnit timeUnit, RandomCodeScene scene, String value) {
        return randomStr(DEFAULT_LENGTH, expired, timeUnit, scene, value);
    }

    default String randomStr(int length, int expired, TimeUnit timeUnit, RandomCodeScene scene, String value) {
        return randomStr(length, expired, timeUnit, scene, "default", value);
    }

    String randomStr(int length, int expired, TimeUnit timeUnit, RandomCodeScene scene, String clientId, String value);

    default boolean isExist(String code, String value, RandomCodeScene scene) {
        return isExist(code, value, "default", scene);
    }

    boolean isExist(String code, String value, String clientId, RandomCodeScene scene);

}
