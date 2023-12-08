package com.hqy.cloud.actuator.service;

/**
 * basic 认证服务
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/7 13:48
 */
public interface BasicAuthorizationService {

    /**
     * 判断请求头是否进行了 HTTP BASIC认证
     * @param authorizationHeader 认证请求头
     * @return                    是否认证通过
     */
    boolean isAuth(String authorizationHeader);

}
