package com.hqy.cloud.gateway.server;

/**
 * CodeAuthorizationChecker.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/21 16:50
 */
public interface CodeAuthorizationChecker {


    /**
     * 校验code是否正确
     * @param resource  resource
     * @param key       key
     * @param code      code
     * @return          result.
     */
    boolean checkCode(String resource, String key, String code);



}
