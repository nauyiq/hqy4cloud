package com.hqy.fundation.common.base.project;

/**
 * 微服务的模块定义。用在@ThriftService注解上面<br>
 * 所有的微服务模块 必须在此常量定义.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/1 10:26
 */
public class MicroServiceConstants {

    /**
     * 全局网关gateway服务
     */
    public static final String GATEWAY = "gateway-service";

    /**
     * 通用的采集服务
     */
    public static final String COMMON_COLLECTOR = "common-collector";

    /**
     * 账号-授权服务
     */
    public static final String ACCOUNT_SERVICE = "account-auth-service";


}