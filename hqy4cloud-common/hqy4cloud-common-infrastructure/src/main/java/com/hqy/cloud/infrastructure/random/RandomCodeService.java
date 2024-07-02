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

    int DEFAULT_LENGTH = 4;


    default String randomNumber(int expired, TimeUnit timeUnit, String... params) {
        return randomNumber(DEFAULT_LENGTH, expired, timeUnit, params);
    }

    /**
     * 生成随机的number code
     * @param length     code长度
     * @param expired    过期时间
     * @param timeUnit   过期时间单位
     * @param params     请求的参数
     * @return           随机生成的code
     */
    String randomNumber(int length, int expired, TimeUnit timeUnit, String... params);


    default String randomStr(int expired, TimeUnit timeUnit, String... params) {
        return randomStr(DEFAULT_LENGTH, expired, timeUnit, params);
    }

    /**
     * 生成随机Str code
     * @param length         code长度
     * @param expired       过期时间
     * @param timeUnit      过期时间单位
     * @param params        请求的参数
     * @return              随机生成的code
     */
    String randomStr(int length, int expired, TimeUnit timeUnit, String... params);

    /**
     * code是否存在
     * @param code           验证code
     * @param params         请求参数
     * @return     result.
     */
    boolean isExist(String code, String... params);


}
