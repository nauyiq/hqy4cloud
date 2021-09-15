package com.hqy.util.spring;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务上下文信息
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-10 19:16
 */
@Data
@Slf4j
public class ProjectContextInfo implements Serializable {

    /**
     * 系统启动时间
     */
    public static long startupTimeMillis = System.currentTimeMillis();

    /**
     * 判断系统是不是刚启动
     */
    private static boolean justStarted = true;

    /**
     * 项目名
     */
    private String appName;

    /**
     * 环境
     */
    private String env = "dev";

    /**
     * 当前服务的主端口号
     */
    private Integer port;

    /**
     * 本机服务的ip地址
     */
    private String hostIp;

    /**
     * 当前服务的辅助端口号，通常是websocket暴露的端口 非必填
     */
    private Integer port2;

    /**
     * 灰白度的值
     */
    private Integer pubValue = 0;

    /**
     * 全局上下文属性定义
     */
    private Map<String, Object> attributes = new ConcurrentHashMap<>();

    public ProjectContextInfo() {
    }

    public ProjectContextInfo(String appName, String env, Integer port, String hostIp, Integer port2, Integer pubValue) {
        this.appName = appName;
        this.env = env;
        this.port = port;
        this.hostIp = hostIp;
        this.port2 = port2;
        this.pubValue = pubValue;
    }

    /**
     * 判断系统是否刚启动不久
     * @return
     */
    public static boolean isJustStarted() {
        return isJustStarted(null);
    }

    /**
     * 判断系统是否刚启动不久
     * @param ignoreMinutes 启动耗时分钟数，多少分钟算是刚启动....
     * @return
     */
    public static boolean isJustStarted(Integer ignoreMinutes) {
        if (justStarted) {
            if (ignoreMinutes == null ||ignoreMinutes < 0) {
                ignoreMinutes = 3;
            }
            long x = System.currentTimeMillis() - startupTimeMillis;
            if (x > ignoreMinutes * 60 * 1000) {
                justStarted = false;
            }
        }
        return justStarted;
    }



}
