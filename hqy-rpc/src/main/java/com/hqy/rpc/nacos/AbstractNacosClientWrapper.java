package com.hqy.rpc.nacos;

import com.facebook.swift.service.ThriftServer;
import com.hqy.base.common.base.lang.ActuatorNodeEnum;
import com.hqy.base.common.base.project.UsingIpPort;
import com.hqy.base.common.swticher.CommonSwitcher;
import com.hqy.rpc.api.AbstractThriftServer;
import com.hqy.rpc.regist.ClusterNode;
import com.hqy.rpc.regist.EnvironmentConfig;
import com.hqy.rpc.regist.GrayWhitePub;
import com.hqy.util.AssertUtil;
import com.hqy.util.spring.ProjectContextInfo;
import com.hqy.util.spring.SpringContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Objects;

/**
 * @author qiyuan.hong
 * @date 2021/8/23 23:26
 */
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
     * 当前节点数据
     */
    private ClusterNode node;


    /**
     * 获取注册到远程服务nacos服务名
     * @return
     */
    public abstract ClusterNode registryProjectClusterNode();


    public boolean isRunning() {
        return isRun;
    }

    /**
     * 声明nacos节点的rpc服务
     * 对于生产者：用于声明RPC服务节点，向nacos暴露ThriftService<br>
     * 对于消费者：用于调度RPC服务节点，从nacos获取ThriftService
     *
     * @param env              系统运行环境
     * @param actuatorNodeEnum 服务节点类型
     * @return                 true 注册并且声明rpc节点成功...
     */
    public boolean declareNodeRpcServer(String env, ActuatorNodeEnum actuatorNodeEnum) {
        //获取子类注册到注册中心 集群节点对象...
        ClusterNode node = registryProjectClusterNode();
        if (StringUtils.isAnyBlank(node.getNameEn(), env)) {
            return false;
        }

        //检测ThriftServer是否启动...
        checkThriftServer(actuatorNodeEnum);
        //根据当前环境. 设置全局环境属性...
        registryGlobalEnvInfo(node);
        //获取当前服务运行的系统信息
        UsingIpPort uip = getProcessingServiceUip(env);
        //注册projectContextInfo方便Spring获取当前环境信息
        declareProjectContextInfo(env, actuatorNodeEnum, node, uip);

        node.setActuatorNode(actuatorNodeEnum);
        node.setUip(uip);
        this.node = node;
        return true;
    }

    private void declareProjectContextInfo(String env, ActuatorNodeEnum actuatorNodeEnum, ClusterNode node, UsingIpPort uip) {
        ProjectContextInfo projectContextInfo = SpringContextHolder.getProjectContextInfo();
        if (StringUtils.isBlank(projectContextInfo.getNameEn())) {
            projectContextInfo = new ProjectContextInfo(node.getNameEn(), env, node.getPubValue(), uip, actuatorNodeEnum);
            //注册ProjectContextInfo 方便Spring获取当前环境信息
            SpringContextHolder.registerContextInfo(projectContextInfo);
        }
    }

    private UsingIpPort getProcessingServiceUip(String env) {
        AbstractThriftServer thriftServer = SpringContextHolder.getBean(AbstractThriftServer.class);
        UsingIpPort usingIpPort = thriftServer.getUsingIpPort();
        AssertUtil.notNull(usingIpPort, "@@@ Get UsingIpPort failure, check bean AbstractThriftServer is ok?");
        usingIpPort.setEnv(env);
        return usingIpPort;
    }

    private void registryGlobalEnvInfo(ClusterNode node) {
        if (EnvironmentConfig.getInstance().isTestEnvironment()) {
            //测试环境 需要严格区分灰度与白度
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
    }


    public ClusterNode getNode() {
        return node;
    }

    /**
     * 检验ThriftServer是否被spring扫描并且启动。。。
     */
    private void checkThriftServer(ActuatorNodeEnum actuatorNodeEnum) {
        if (actuatorNodeEnum == ActuatorNodeEnum.CONSUMER) {
            //如果是服务的消费者... 即不提供rpc方法的服务 直接return
            return;
        }
        //判断RPC服务是否启动
        ThriftServer tServer = SpringContextHolder.getBean(ThriftServer.class);
        boolean running = tServer.isRunning();
        if (!running) {
            //如果没有扫描到server 手动启动一下
            tServer.start();
        }
        log.info("@@@ Get ThriftServer success, running:{}", running);
    }

}
