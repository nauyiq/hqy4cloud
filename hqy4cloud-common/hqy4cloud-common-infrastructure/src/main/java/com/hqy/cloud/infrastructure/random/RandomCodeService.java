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


    default String randomNumber(int expired, TimeUnit timeUnit, RandomCodeScene scene, String value) {
        return randomNumber(DEFAULT_LENGTH, expired, timeUnit, scene, value);
    }

    /**
     * 生成随机的number code
     * @param length     code长度
     * @param expired    过期时间
     * @param timeUnit   过期时间单位
     * @param scene      场景
     * @param value      缓存的值
     * @return           随机生成的code
     */
    String randomNumber(int length, int expired, TimeUnit timeUnit, RandomCodeScene scene, String value);


    default String randomStr(int expired, TimeUnit timeUnit, RandomCodeScene scene, String value) {
        return randomStr(DEFAULT_LENGTH, expired, timeUnit, scene, value);
    }

    /**
     * 生成随机Str code
     * @param length         code长度
     * @param expired       过期时间
     * @param timeUnit      过期时间单位
     * @param scene         场景
     * @param value         缓存的值
     * @return              随机生成的code
     */
    String randomStr(int length, int expired, TimeUnit timeUnit, RandomCodeScene scene, String value);

    /**
     * code是否存在
     * @param code           验证code
     * @param value          保存的值
     * @param scene          场景
     * @return     result.
     */
    boolean isExist(String code, String value, RandomCodeScene scene);


}
