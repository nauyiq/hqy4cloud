package com.hqy.cloud.rpc.nacos.discovery;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.hqy.cloud.rpc.nacos.naming.NamingServiceWrapper;
import com.hqy.cloud.util.spring.SpringContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.hqy.cloud.common.base.config.ConfigConstants.NACOS_GROUP;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/11/20 13:59
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NacosDiscovery {
    private final NacosServiceManager nacosServiceManager;
    private final NacosDiscoveryProperties nacosDiscoveryProperties;
    private final Environment environment;
    private volatile NamingServiceWrapper wrapper;
    private volatile Instance selfInstance;

    public NamingService getService() {
        return nacosServiceManager.getNamingService();
    }

    public NamingMaintainService getMaintainService() {
        return nacosServiceManager.getNamingMaintainService(nacosDiscoveryProperties.getNacosProperties());
    }

    public NamingServiceWrapper getServiceWrapper() {
        if (wrapper == null) {
            String group = environment.getProperty(NACOS_GROUP, Constants.DEFAULT_GROUP);
            this.wrapper = new NamingServiceWrapper(getService(), getMaintainService(), group);
        }
        return this.wrapper;
    }

    public Instance getSelfInstance() {
        if (selfInstance != null) {
            return selfInstance;
        }
        String nameEn = SpringContextHolder.getProjectContextInfo().getNameEn();
        try {
            List<Instance> instances = getServiceWrapper().selectInstances(nameEn, true);
            List<Instance> toList = instances.stream().filter(instance -> nacosDiscoveryProperties.getIp().equals(instance.getIp())
                    && nacosDiscoveryProperties.getPort() == instance.getPort()).toList();
            if (CollectionUtils.isNotEmpty(toList)) {
                selfInstance = toList.get(0);
            }
            return selfInstance;
        } catch (NacosException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }


}
