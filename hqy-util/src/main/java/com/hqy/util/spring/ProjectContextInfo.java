package com.hqy.util.spring;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hqy.fundation.common.base.lang.ActuatorNodeEnum;
import com.hqy.fundation.common.base.project.UsingIpPort;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务上下文信息
 * @author qy
 * @date  2021-08-10 19:16
 */
@Data
@Slf4j
public class ProjectContextInfo implements Serializable {

    @JsonIgnore
    private static final long serialVersionUID = -3512823069773039476L;
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
    private String nameEn;

    /**
     * 环境
     */
    private String env = "dev";

    /**
     * 灰白度的值
     */
    private Integer pubValue = 0;

    /**
     * 端口等信息
     */
    private UsingIpPort uip;


    /**
     * 节点类型
     */
    private ActuatorNodeEnum nodeType;

    /**
     * 全局上下文属性定义
     */
    private Map<String, Object> attributes = new ConcurrentHashMap<>();


    public ProjectContextInfo() {
    }

    public ProjectContextInfo(String nameEn, String env, Integer pubValue, UsingIpPort uip, ActuatorNodeEnum nodeType) {
        this.nameEn = nameEn;
        this.env = env;
        this.pubValue = pubValue;
        this.uip = uip;
        this.nodeType = nodeType;
    }

    /**
     * 判断系统是否刚启动不久
     * @return
     */
    public boolean isJustStarted() {
        return isJustStarted(null);
    }


    /**
     * 判断系统是否刚启动不久
     * @param ignoreMinutes 启动耗时分钟数，多少分钟算是刚启动....
     * @return
     */
    public boolean isJustStarted(Integer ignoreMinutes) {
        if (justStarted) {
            if (ignoreMinutes == null || ignoreMinutes < 0) {
                ignoreMinutes = 3;
            }
            long x = System.currentTimeMillis() - startupTimeMillis;
            if (x > ignoreMinutes * 60 * 1000) {
                justStarted = false;
            }
        }
        return justStarted;
    }

    @JsonIgnore
    public Map<String, Object> getAttributes() {
        return attributes;
    }


}
