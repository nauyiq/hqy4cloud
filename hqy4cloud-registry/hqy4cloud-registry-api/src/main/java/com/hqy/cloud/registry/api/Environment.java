package com.hqy.cloud.registry.api;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 系统初始化的时候 记录下当前环境信息<br>
 * @author qy
 * @date 2021-08-16
 */
@Slf4j
public class Environment {

    /**
     * 开发环境
     */
    public static final String ENV_DEV = "dev";

    /**
     * 测试环境
     */
    public static final String ENV_TEST = "test";

    /**
     * UAT 预生产环境
     */
    public static final String ENV_UAT_PRE_PROD = "uat";

    /**
     * 生产环境
     */
    public static final String ENV_PROD = "prod";

    /**
     * 标记 rpc 功能弱化的节点，不对外提供rpc服务的独立的节点(可以少一些rpc的io线程)。
     * 由框架自身自动维护，业务代码不用设置这个的值....
     */
    public static boolean FLAG_RPC_REDUCED_SERVICE = false;

    /**
     * IO 密集型rpc服务？默认false; 如果true，rpc节点处理线程再翻倍...
     */
    public static boolean FLAG_IO_INTENSIVE_RPC_SERVICE = false;

    private String env;

    public Environment() {
    }

    public Environment(String env) {
        this.env = env;
    }

    public String getEnvironment() {
        if (StringUtils.isBlank(env)) {
            env = ENV_DEV;
        }
        //兼容大小写
        return env.toLowerCase();
    }

    public void setEnvironment(String env) {
        if (StringUtils.isBlank(env)) {
            env = ENV_DEV;
        }
        this.env = env;
    }

    public boolean isDevTestEnvironment() {
        return ENV_TEST.equalsIgnoreCase(env) || ENV_DEV.equalsIgnoreCase(env);
    }

    public boolean isDevEnvironment() {
        return ENV_DEV.equalsIgnoreCase(env);
    }

    public boolean isTestEnvironment() {
        return ENV_TEST.equalsIgnoreCase(env);
    }

    public boolean isUatEnvironment() {
        return ENV_UAT_PRE_PROD.equalsIgnoreCase(env);
    }

    public boolean isProdEnvironment() {
        return ENV_PROD.equalsIgnoreCase(env);
    }


}
