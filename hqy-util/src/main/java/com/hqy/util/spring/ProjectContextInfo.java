package com.hqy.util.spring;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hqy.fundation.common.base.lang.ActuatorNodeEnum;
import com.hqy.fundation.common.base.lang.BaseMathConstants;
import com.hqy.fundation.common.base.lang.BaseStringConstants;
import com.hqy.fundation.common.base.project.UsingIpPort;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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


    /**
     * nacos的注册元数据key
     */
    @JsonIgnore
    public static final transient String NODE_INFO = "nodeInfo";

    /**
     * white白名单ip
     */
    public static final String WHITE_IP_PROPERTIES_KEY = "MANUAL_WHITE_IP";

    /**
     * white白名单uri
     */
    public static final String WHITE_URI_PROPERTIES_KEY = "MANUAL_WHITE_URI";

    /**
     * 手动黑名单列表
     */
    public static final String MANUAL_BLOCKED_IP_KEY = "MANUAL_BLOCK_IP";

    /**
     * bi分析黑名单列表
     */
    public static final String BI_BLOCKED_IP_KEY = "BI_BLOCK_IP";


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
            if (x > ignoreMinutes * BaseMathConstants.ONE_MINUTES_4MILLISECONDS) {
                justStarted = false;
            }
        }
        return justStarted;
    }

    /**
     * 是否使用Linux服务器支持的Epoll机制
     */
    private static Boolean isUseLinuxNativeEpoll;

    public static boolean isUseLinuxNativeEpoll() {
        if (Objects.nonNull(isUseLinuxNativeEpoll)) {
            return isUseLinuxNativeEpoll;
        }
        String osName = System.getProperty("os.name");
        String osArch = System.getProperty("os.arch");
        //windows系统或者是arm系统
        //使用标准的linux epoll机制
        isUseLinuxNativeEpoll = !osName.startsWith("Windows") && !osArch.startsWith("aarch64");

        log.info("\r\n##### initialize: isUseLinuxNativeEpoll ={}  \t ### ### ", isUseLinuxNativeEpoll);

        return isUseLinuxNativeEpoll;
    }

    @JsonIgnore
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @SuppressWarnings("unchecked")
    public Set<String> getAttributeSetString(String key) {
        if (StringUtils.isBlank(key)) {
            log.warn("@@@ 上下文对象获取getAttributeSet, key is blank.");
            return new HashSet<>();
        } else {
            try {
                return (Set<String>) attributes.getOrDefault(key, new HashSet<>());
            } catch (Exception e) {
                log.error("@@@ 上下文对象获取getAttributeSet异常, key = {},  {}", key, e.getMessage());
                return new HashSet<>();
            }
        }
    }



    public String getNameWithIpPort() {
        return nameEn.concat(BaseStringConstants.Symbol.AT)
                .concat(this.getUip().getIp())
                .concat(BaseStringConstants.Symbol.COLON)
                .concat(this.getUip().getPort() + "");
    }


    public void setProperties(String key, Object data) {
        this.attributes.put(key, data);
    }
}
