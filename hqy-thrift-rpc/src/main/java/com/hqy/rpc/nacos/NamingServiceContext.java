package com.hqy.rpc.nacos;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.naming.NamingService;
import com.hqy.fundation.common.swticher.CommonSwitcher;
import com.hqy.util.spring.ProjectContextInfo;
import com.hqy.util.spring.SpringContextHolder;

import java.util.Map;

/**
 * NamingService nacos客户端工具类，
 * 当前服务注册进nacos 会初始化此类.
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-09-18 11:28
 */
public class NamingServiceContext {

    private static NamingService namingService = null;

    private static final NamingServiceContext instance = new NamingServiceContext();

    private NamingServiceContext() {}

    public NamingServiceContext getInstance() {return instance;}

    private  static boolean close = false;


    public static NamingService getNamingService() {
        if (namingService == null && !close) {
            synchronized (NamingServiceContext.class) {
                if (namingService == null) {
                    namingService = buildNamingService();
                }
            }
        }
        return namingService;
    }

    public static void close() {
        close = true;
        namingService = null;
    }

    private static NamingService buildNamingService() {
        if (CommonSwitcher.ENABLE_SPRING_CONTEXT.isOff()) {
            ProjectContextInfo info = SpringContextHolder.getProjectContextInfo();
            Map<String, Object> attributes = info.getAttributes();
            namingService = (NamingService) attributes.get(ProjectContextInfo.AttributesKey.NACOS_NAMING_SERVICE);
        } else {
            NacosDiscoveryProperties nacosDiscoveryProperties = SpringContextHolder.getBean(NacosDiscoveryProperties.class);
            namingService = nacosDiscoveryProperties.namingServiceInstance();
        }

        if (namingService == null) {
            throw new IllegalArgumentException("Get NamingService failure, check service registration results.");
        }

        return namingService;
    }





}
