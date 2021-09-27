package com.hqy.rpc.nacos;

import com.hqy.fundation.common.swticher.CommonSwitcher;
import com.hqy.rpc.regist.EnvironmentConfig;
import com.hqy.rpc.regist.GrayWhitePub;
import com.hqy.util.spring.ProjectContextInfo;
import com.hqy.util.spring.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * @author qy
 * @create 2021/8/23 23:26
 */
@Component
public abstract class AbstractNacosClientWriter {

    private static final Logger log = LoggerFactory.getLogger(AbstractNacosClientWriter.class);

    /**
     * 当前服务节点
     */
    private NacosNode node = null;

    /**
     * 是否启动...
     */
    private transient boolean isRun = false;

    @PreDestroy
    public void destroy() {




    }


    public boolean createThriftServer(NacosNode nacosNode) {
        if (EnvironmentConfig.getInstance().isTestEnvironment()) {
            //测试环境 //需要严格区分灰度与白度
            nacosNode.setPubValue(GrayWhitePub.WHITE.value);
            CommonSwitcher.ENABLE_GRAY_MECHANISM.setStatus(true);
        } else if (EnvironmentConfig.getInstance().isDevEnvironment()) {
            // 开发环境
            nacosNode.setPubValue(GrayWhitePub.GRAY.value);
            CommonSwitcher.ENABLE_GRAY_MECHANISM.setStatus(false);
        } else if (EnvironmentConfig.getInstance().isUatEnvironment()) {
            // uat环境
            nacosNode.setPubValue(GrayWhitePub.GRAY.value);
            CommonSwitcher.ENABLE_GRAY_MECHANISM.setStatus(false);
        } else {
            //开发环境
            nacosNode.setPubValue(GrayWhitePub.WHITE.value);
            CommonSwitcher.ENABLE_GRAY_MECHANISM.setStatus(true);
        }

        log.info("@@@ GrayWhitePub:{}, ENABLE_GRAY_MECHANISM:{}", nacosNode.getPubValue(), CommonSwitcher.ENABLE_GRAY_MECHANISM.getStatus());

        //注册projectContextInfo 上下文对象 方便spring获取当前环境信息
        ProjectContextInfo contextInfo = SpringContextHolder.getProjectContextInfo();

        this.node = node;

        if (this.node == null) {
            throw new IllegalStateException("@@@ ERROR, Parameter node is null.");
        }



        return false;


    }


    public NacosNode getNode() {
        return node;
    }

    public void setNode(NacosNode node) {
        this.node = node;
    }
}
