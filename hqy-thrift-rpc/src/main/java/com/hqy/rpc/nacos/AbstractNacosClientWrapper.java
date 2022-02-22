package com.hqy.rpc.nacos;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.discovery.NacosWatch;
import com.hqy.fundation.common.base.lang.ActuatorNodeEnum;
import com.hqy.fundation.common.base.lang.BaseStringConstants;
import com.hqy.fundation.common.base.project.UsingIpPort;
import com.hqy.fundation.common.swticher.CommonSwitcher;
import com.hqy.rpc.regist.ClusterNode;
import com.hqy.rpc.regist.EnvironmentConfig;
import com.hqy.rpc.regist.GrayWhitePub;
import com.hqy.util.AssertUtil;
import com.hqy.util.JsonUtil;
import com.hqy.util.spring.ProjectContextInfo;
import com.hqy.util.spring.SpringContextHolder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;

/**
 * @author qiyuan.hong
 * @date 2021/8/23 23:26
 */
@Data
@Component
public abstract class AbstractNacosClientWrapper {

    private static final Logger log = LoggerFactory.getLogger(AbstractNacosClientWrapper.class);

    /**
     * 是否启动...
     */
    private transient boolean isRun = false;

    @PreDestroy
    public void destroy() {
        NamingServiceClient.getInstance().close();
        isRun = false;
    }

    /**
     * 获取注册到远程服务nacos服务名
     * @return
     */
    public abstract String getProjectName();

    public boolean isRunning() {
        return isRun;
    }

    /**
     * 声明nacos节点的rpc服务
     * @param node nacos服务节点
     * @param env 项目环境
     * @return
     */
    public boolean declareNodeRpcServer(ClusterNode node, String env) {
        return declareNodeRpcServer(node, env, ActuatorNodeEnum.CONSUMER);
    }

    /**
     * 声明nacos节点的rpc服务
     * 对于生产者：用于声明RPC服务节点，向nacos暴露ThriftService<br>
     * 对于消费者：用于调度RPC服务节点，从nacos获取ThriftService
     * @param node
     * @param env
     * @param actuatorNodeEnum
     * @return
     */
    public boolean declareNodeRpcServer(ClusterNode node, String env, ActuatorNodeEnum actuatorNodeEnum) {

        String nodeName = node.getNameEn();
        AssertUtil.notEmpty(nodeName, "@@@ Service name can not empty!");
        AssertUtil.notEmpty(env, "@@@ Project running environment can not empty!");

        if (EnvironmentConfig.getInstance().isTestEnvironment()) {
            //测试环境 //需要严格区分灰度与白度
            node.setPubValue(GrayWhitePub.WHITE.value);
            CommonSwitcher.ENABLE_GRAY_MECHANISM.setStatus(true);
        } else if (EnvironmentConfig.getInstance().isDevEnvironment()) {
            // 开发环境
            node.setPubValue(GrayWhitePub.GRAY.value);
            CommonSwitcher.ENABLE_GRAY_MECHANISM.setStatus(false);
        } else if (EnvironmentConfig.getInstance().isUatEnvironment()) {
            // uat环境
            node.setPubValue(GrayWhitePub.GRAY.value);
            CommonSwitcher.ENABLE_GRAY_MECHANISM.setStatus(false);
        } else {
            //生产环境
            node.setPubValue(GrayWhitePub.WHITE.value);
            CommonSwitcher.ENABLE_GRAY_MECHANISM.setStatus(true);
        }

        log.info("@@@ GrayWhitePub:{}, ENABLE_GRAY_MECHANISM:{}", node.getPubValue(), CommonSwitcher.ENABLE_GRAY_MECHANISM.getStatus());

        //注册projectContextInfo方便Spring获取当前环境信息
        ProjectContextInfo projectContextInfo = SpringContextHolder.getProjectContextInfo();
        if (StringUtils.isBlank(projectContextInfo.getNameEn())) {
            //获取端口信息等
            projectContextInfo = new ProjectContextInfo(nodeName, env, node.getPubValue(), node.getUip(), actuatorNodeEnum);
            //注册ProjectContextInfo 方便Spring获取当前环境信息
            SpringContextHolder.registerContextInfo(projectContextInfo);
        }

        return true;
    }


    /**
     * 加载rpc端口和配置文件中的metadata原数据 并注册到nacos服务
      * @param properties NacosDiscoveryProperties
     * @return NacosWatch
     */
    @Bean
    @ConditionalOnMissingBean
    @DependsOn("thriftServer")
    public NacosWatch nacosWatch(NacosDiscoveryProperties properties) {
        //原来的元数据全部清空
        properties.setMetadata(new HashMap<>(16));
        //更改服务详情中的元数据
        ClusterNode node = new ClusterNode();
        UsingIpPort uip = SpringContextHolder.getProjectContextInfo().getUip();
        node.setUip(uip);
        node.setNameEn(SpringContextHolder.getProjectContextInfo().getNameEn());
        node.setPubValue(SpringContextHolder.getProjectContextInfo().getPubValue());
        node.setName(getProjectName());
        //TODO HASH因子等设置
        Map<String, String> metadata = properties.getMetadata();
        metadata.put(BaseStringConstants.NODE_INFO, JsonUtil.toJson(node));
        properties.setMetadata(metadata);
        return new NacosWatch(properties);
    }



}
