package com.hqy.cloud.util.spring;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hqy.cloud.common.base.lang.ActuatorNodeEnum;
import com.hqy.cloud.common.base.lang.NumberConstants;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.base.project.UsingIpPort;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.util.JsonUtil;
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
    private UsingIpPort uip;

    /**
     * 节点类型
     */
    private ActuatorNodeEnum nodeType;

    /**
     * 全局上下文属性定义
     */
    private Map<String, Object> attributes = new ConcurrentHashMap<>();


    private static Map<Class<?>, Object> beansMap = new ConcurrentHashMap<>();


    /**
     * nacos的注册元数据key
     */
    public static final transient String NODE_INFO = "nodeInfo";

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
        return nameEn.concat(StringConstants.Symbol.AT)
                .concat(this.getUip().getHostAddr())
                .concat(StringConstants.Symbol.COLON)
                .concat(this.getUip().getPort() + "");
    }


    public static void startPrintf() {
        if (CommonSwitcher.ENABLE_SPRING_BOOT_RESTART_DEVTOOLS.isOn()) {
              /*
        必须禁用springboot热部署 否则会导致 thrift rpc 调用时thriftMethodManager 进行codec 编码struct类时 抛出类转换异常。
        原因就是springboot 热部署会破坏类加载器的双亲委派机制 即springboot通过强行干预-- “截获”了用户自定义类的加载。
        （由jvm的加载器AppClassLoader变为springboot自定义的加载器RestartClassLoader，一旦发现类路径下有文件的修改，
        springboot中的spring-boot-devtools模块会立马丢弃原来的类文件及类加载器，重新生成新的类加载器来加载新的类文件，从而实现热部署。
        导致需要两个类对象的类全称虽然一致 但是类加载器不一致
         */
            System.setProperty("spring.devtools.restart.enabled", "false");
        }
        ProjectContextInfo projectContextInfo = SpringContextHolder.getProjectContextInfo();
        log.info("############################## ############### ############### ###############");
        log.info("##### Server Started OK : uip = {} ", JsonUtil.toJson(projectContextInfo.getUip()));
        log.info("##### Server Started OK. serviceName = {}", projectContextInfo.getNameEn());
        log.info("############################## ############### ############### ###############");
    }


    public void setProperties(String key, Object data) {
        this.attributes.put(key, data);
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
}
