package com.hqy.rpc.nacos;

import com.hqy.fundation.common.base.lang.ActuatorNodeEnum;
import com.hqy.fundation.common.base.project.UsingIpPort;
import com.hqy.fundation.common.swticher.CommonSwitcher;
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
    public abstract ClusterNode setProjectClusterNode();


    public boolean isRunning() {
        return isRun;
    }

    /**
     * 声明nacos节点的rpc服务
     * 对于生产者：用于声明RPC服务节点，向nacos暴露ThriftService<br>
     * 对于消费者：用于调度RPC服务节点，从nacos获取ThriftService
     *
     * @param env
     * @param actuatorNodeEnum
     * @return
     */
    public boolean declareNodeRpcServer(String env, ActuatorNodeEnum actuatorNodeEnum) {

        ClusterNode node = setProjectClusterNode();
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

        UsingIpPort uip = node.getUip();
        uip.setEnv(env);

        //注册projectContextInfo方便Spring获取当前环境信息
        ProjectContextInfo projectContextInfo = SpringContextHolder.getProjectContextInfo();
        if (StringUtils.isBlank(projectContextInfo.getNameEn())) {
            //获取端口信息等
            projectContextInfo = new ProjectContextInfo(nodeName, env, node.getPubValue(), uip, actuatorNodeEnum);
            //注册ProjectContextInfo 方便Spring获取当前环境信息
            SpringContextHolder.registerContextInfo(projectContextInfo);
        }

        node.setActuatorNode(actuatorNodeEnum);
        node.setUip(uip);

        this.node = node;
        return true;
    }


    public ClusterNode getNode() {
        return node;
    }

}
