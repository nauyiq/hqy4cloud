package com.hqy.rpc.nacos;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.discovery.NacosWatch;
import com.hqy.fundation.common.base.lang.BaseStringConstants;
import com.hqy.fundation.common.base.project.MicroServiceHelper;
import com.hqy.fundation.common.base.project.UsingIpPort;
import com.hqy.rpc.regist.ClusterNode;
import com.hqy.rpc.regist.EnvironmentConfig;
import com.hqy.util.JsonUtil;
import com.hqy.util.spring.ProjectContextInfo;
import com.hqy.util.spring.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 配置注册到nacos中的元数据. 并且声明nacos节点数据
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/1 14:53
 */
@Slf4j
@Component
public class NacosWatchConfig {

    @Value("${spring.application.name}")
    private String nameEn;

    /**
     * 加载rpc端口和配置文件中的metadata原数据 并注册到nacos服务
     * @param properties NacosDiscoveryProperties
     * @return NacosWatch
     */
    @Bean
    @ConditionalOnMissingBean
    public NacosWatch nacosWatch(NacosDiscoveryProperties properties) {

        //nacos客户端
        AbstractNacosClientWrapper clientWrapper = SpringContextHolder.getBean(AbstractNacosClientWrapper.class);
        String environment = EnvironmentConfig.getInstance().getEnvironment();

        //声明nacos节点 并且注册上下文到springContextHolder中.
        boolean result = clientWrapper.declareNodeRpcServer(environment, MicroServiceHelper.getNodeType(nameEn));

        log.info("@@@ DeclareNodeRpcServer end. result:{}", result);

        //获取节点信息.
        ClusterNode clusterNode = clientWrapper.getNode();
        UsingIpPort uip = SpringContextHolder.getProjectContextInfo().getUip();
        clusterNode.setUip(uip);
        clusterNode.setPubValue(SpringContextHolder.getProjectContextInfo().getPubValue());

        //TODO HASH因子等设置

        //更改服务详情中的元数据
        Map<String, String> metadata = properties.getMetadata();
        metadata.put(ProjectContextInfo.NODE_INFO, JsonUtil.toJson(clusterNode));
        properties.setMetadata(metadata);
        return new NacosWatch(properties);
    }

}
