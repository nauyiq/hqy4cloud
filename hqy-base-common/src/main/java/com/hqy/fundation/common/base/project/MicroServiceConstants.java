package com.hqy.fundation.common.base.project;

/**
 * 微服务的模块定义。用在@ThriftService注解上面<br>
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/1 10:26
 */
public class MicroServiceConstants {

    /**
     * 全局网关gateway服务
     */
    public static final String GATEWAY = "gateway_service";

    /**
     * 通用的采集服务
     */
    public static final String COMMON_COLLECTOR = "common_collector";

    /**
     * 账号 授权服务
     */
    public static final String ACCOUNT_SERVICE = "account_auth_service";


}
