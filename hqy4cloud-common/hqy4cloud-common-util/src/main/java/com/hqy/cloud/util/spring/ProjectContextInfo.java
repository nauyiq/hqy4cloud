package com.hqy.cloud.util.spring;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hqy.cloud.common.base.lang.ActuatorNode;
import com.hqy.cloud.common.base.lang.NumberConstants;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.base.project.UsingIpPort;
import com.hqy.cloud.util.NetUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务上下文信息
 * @author qy
 * @date  2021-08-10 19:16
 */
@Data
@Slf4j
public class ProjectContextInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = -3512823069773039476L;

    /**
     * 系统启动时间
     */
    private long startupTimeMillis = System.currentTimeMillis();

    /**
     * 判断系统是不是刚启动
     */
    private boolean justStarted = true;

    /**
     * 是否使用Linux服务器支持的Epoll机制
     */
    private static Boolean isUseLinuxNativeEpoll;

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
    private UsingIpPort uip = new UsingIpPort(NetUtils.getProgramId());

    /**
     * 节点类型
     */
    private ActuatorNode nodeType;

    /**
     * 版本revision
     */
    private String revision;


    /**
     * metadata
     */
    private Map<String, String> metadata = new ConcurrentHashMap<>();


    private static Map<Class<?>, Object> beansMap = new ConcurrentHashMap<>();

    /**
     * white白名单ip
     */
    public static final transient String WHITE_IP_PROPERTIES_KEY = "MANUAL_WHITE_IP";

    /**
     * 手动黑名单列表
     */
    public static final transient String MANUAL_BLOCKED_IP_KEY = "MANUAL_BLOCK_IP";

    /**
     * bi分析黑名单列表
     */
    public static final transient String BI_BLOCKED_IP_KEY = "BI_BLOCK_IP";


    public ProjectContextInfo() {
    }

    public ProjectContextInfo(String nameEn, String env, Integer pubValue, UsingIpPort uip,  ActuatorNode nodeType) {
        this.nameEn = nameEn;
        this.env = env;
        this.pubValue = pubValue;
        this.uip = uip;
        this.nodeType = nodeType;
    }

    public boolean isJustStarted() {
        return isJustStarted(null);
    }

    public boolean isJustStarted(Integer ignoreMinutes) {
        if (justStarted) {
            if (ignoreMinutes == null || ignoreMinutes < 0) {
                ignoreMinutes = 3;
            }
            long x = System.currentTimeMillis() - startupTimeMillis;
            if (x > ignoreMinutes * NumberConstants.ONE_MINUTES_4MILLISECONDS) {
                justStarted = false;
            }
        }
        return justStarted;
    }


    public static boolean isUseLinuxNativeEpoll() {
        if (Objects.nonNull(isUseLinuxNativeEpoll)) {
            return isUseLinuxNativeEpoll;
        }
        String osName = System.getProperty(StringConstants.OS_NAME_KEY);
        String osArch = System.getProperty(StringConstants.OS_ARCH_KEY);
        //windows系统或者是arm系统 使用标准的linux epoll机制
        isUseLinuxNativeEpoll = !osName.startsWith(StringConstants.OS_WINDOWS_PREFIX) && !osArch.startsWith(StringConstants.OS_ARCH_PREFIX);
        log.info("@@@ Initialize isUseLinuxNativeEpoll:{}", isUseLinuxNativeEpoll);
        return isUseLinuxNativeEpoll;
    }


    public String getNameWithIpPort() {
        return nameEn.concat(StringConstants.Symbol.AT)
                .concat(this.getUip().getHostAddr())
                .concat(StringConstants.Symbol.COLON)
                .concat(this.getUip().getPort() + "");
    }


    public static Map<Class<?>, Object> getBeansMap(){
        return beansMap;
    }

    @JsonIgnore
    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> clazz) {
        if(beansMap.containsKey(clazz)) {
            return (T) beansMap.get(clazz);
        }
        return null;
    }

    public static void setBean(Object bean) {
        setBean(bean.getClass(), bean);
    }

    public static void setBean(Class<?> clazz ,Object bean) {
        beansMap.put(clazz, bean);
    }

    public void registrySocketIoPort(int port) {
        if (this.getUip() != null) {
            getUip().setSocketPort(port);
        }
    }

    /**
     * 是否是本地负载因子
     * @param factor      本地负载因子
     * @param serviceName 服务名
     * @return            result
     */
    public boolean isLocalFactor(String factor, String serviceName) {
        if (!this.nameEn.equals(serviceName)) {
            return false;
        }
        if (StringConstants.DEFAULT.equals(factor)) {
            return true;
        }

        try {
            String[] split = factor.split(StringConstants.Symbol.COLON);
            return split[0].equals(this.uip.getHostAddr());
        } catch (Throwable cause) {
            return false;
        }
    }
}
