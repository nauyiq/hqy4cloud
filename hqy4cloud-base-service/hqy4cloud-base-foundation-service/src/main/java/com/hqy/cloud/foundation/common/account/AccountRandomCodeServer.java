package com.hqy.cloud.foundation.common.account;

/**
 * RedisRandomCodeServer.
 * Generator Random code.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/14 17:06
 */
public interface AccountRandomCodeServer {

    int DEFAULT_LENGTH = 4;
    int DEFAULT_SECONDS = 10 * 60;

    /**
     * 生成随机code
     * @param username 用户名.
     * @param email    邮箱.
     * @return random code.
     */
    String randomCode(String username, String email);



    /**
     * 生成随机code
     * @param username 用户名
     * @param email    邮箱
     * @param length   code长度
     * @return random code.
     */
    String randomCode(String username, String email, int length);

    /**
     * 生成随机code
     * @param username        用户名
     * @param email           邮箱
     * @param length          code长度
     * @param expiredSeconds  失效时间. 单位秒
     * @return
     */
    String randomCode(String username, String email, int length, int expiredSeconds);

    /**
     * code是否存在
     * @param username       用户名
     * @param email          邮箱
     * @param code           验证code
     * @return     result.
     */
    boolean isExist(String username, String email, String code);


}
