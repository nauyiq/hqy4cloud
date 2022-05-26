package com.hqy.rpc.nacos;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.discovery.NacosWatch;
import com.hqy.base.common.base.project.MicroServiceManager;
import com.hqy.base.common.base.project.UsingIpPort;
import com.hqy.rpc.regist.ClusterNode;
import com.hqy.rpc.regist.EnvironmentConfig;
import com.hqy.util.JsonUtil;
import com.hqy.util.spring.ProjectContextInfo;
import com.hqy.util.spring.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 配置注册到nacos中的元数据. 并且声明nacos节点数据
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/1 14:53
 */
@Slf4j
@Configuration
public class NacosWatchConfig {

    @Value("${spring.application.name}")
    private String nameEn;

    @Resource
    private AbstractNacosClientWrapper clientWrapper;

    /**
     * 加载rpc端口和配置文件中的metadata原数据 并注册到nacos服务
     * @param properties NacosDiscoveryProperties
     * @return NacosWatch
     */
    @Bean
    @ConditionalOnMissingBean
    public NacosWatch nacosWatch(NacosServiceManager nacosServiceManager, NacosDiscoveryProperties properties) {
        //获取当前服务运行的上下文环境.
        String environment = EnvironmentConfig.getInstance().getEnvironment();

        //声明nacos节点 并且注册上下文到springContextHolder中.
        boolean result = clientWrapper.declareNodeRpcServer(environment, MicroServiceManager.getNodeType(nameEn));
        if (!result) {
            log.error("@@@ 声明当前nacos节点node信息 注册ProjectContext失败. nameEn:{}, env:{}", nameEn, environment);
            //直接结束当前进程.
            System.exit(0);
        }

        //获取节点信息.
        ClusterNode clusterNode = clientWrapper.getNode();
        UsingIpPort uip = SpringContextHolder.getProjectContextInfo().getUip();
        clusterNode.setUip(uip);
        clusterNode.setPubValue(SpringContextHolder.getProjectContextInfo().getPubValue());
        //更改服务详情中的元数据
        Map<String, String> metadata = properties.getMetadata();
        metadata.put(ProjectContextInfo.NODE_INFO, JsonUtil.toJson(clusterNode));
        properties.setMetadata(metadata);
        return new NacosWatch(nacosServiceManager, properties);
    }

}
