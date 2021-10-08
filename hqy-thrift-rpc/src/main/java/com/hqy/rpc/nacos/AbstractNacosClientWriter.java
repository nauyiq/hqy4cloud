package com.hqy.rpc.nacos;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.discovery.NacosWatch;
import com.hqy.fundation.common.swticher.CommonSwitcher;
import com.hqy.rpc.regist.EnvironmentConfig;
import com.hqy.rpc.regist.GrayWhitePub;
import com.hqy.util.spring.ProjectContextInfo;
import com.hqy.util.spring.SpringContextHolder;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.PreDestroy;
import java.util.Map;

/**
 * @author qy
 * @create 2021/8/23 23:26
 */
@Data
@Configuration
public abstract class AbstractNacosClientWriter {

    private static final Logger log = LoggerFactory.getLogger(AbstractNacosClientWriter.class);

    /**
     * 服务的英文名
     */
    @Value("${spring.application.name:defaultApp}")
    private String nameEn;

    /**
     * 当前服务的http端口号
     */
    @Value("${server.port:8080}")
    private int port;

    /**
     * 当前服务的rpc端口
     */
    private static int usingPort;

    /**
     * 是否启动...
     */
    private transient boolean isRun = false;

    @PreDestroy
    public void destroy() {

    }

    public abstract String getProjectName();


    public void createNacosNode(XxNode xxNode) {
        int pubValue;
        if (EnvironmentConfig.getInstance().isTestEnvironment()) {
            //测试环境 //需要严格区分灰度与白度
            pubValue = GrayWhitePub.WHITE.value;
            CommonSwitcher.ENABLE_GRAY_MECHANISM.setStatus(true);
        } else if (EnvironmentConfig.getInstance().isDevEnvironment()) {
            // 开发环境
            pubValue = GrayWhitePub.GRAY.value;
            CommonSwitcher.ENABLE_GRAY_MECHANISM.setStatus(false);
        } else if (EnvironmentConfig.getInstance().isUatEnvironment()) {
            // uat环境
            pubValue = GrayWhitePub.GRAY.value;
            CommonSwitcher.ENABLE_GRAY_MECHANISM.setStatus(false);
        } else {
            //生产环境
            pubValue = GrayWhitePub.WHITE.value;
            CommonSwitcher.ENABLE_GRAY_MECHANISM.setStatus(true);
        }

        log.info("@@@ GrayWhitePub:{}, ENABLE_GRAY_MECHANISM:{}", pubValue, CommonSwitcher.ENABLE_GRAY_MECHANISM.getStatus());

        NacosNodeUtil.getInstance().buildNodeInfo(pubValue, nameEn, port, usingPort, xxNode);

    }


    public boolean createThriftServer() {


        //注册projectContextInfo 上下文对象 方便spring获取当前环境信息
        ProjectContextInfo contextInfo = SpringContextHolder.getProjectContextInfo();


        if (this.node == null) {
            throw new IllegalStateException("@@@ ERROR, Parameter node is null.");
        }



        return false;


    }

    /**
     * 加载rpc端口和配置文件中的metadata原数据 并注册到nacos服务
     * @param properties
     * @return
     */
    @Bean
    @DependsOn("thriftServer")
    public NacosWatch nacosWatch(NacosDiscoveryProperties properties) {
        //清除掉所有数据
        Map<String, String> metaData = nacosMetaDataProperties.getMetaData();
        metaData.put("created", "" + System.currentTimeMillis());
        metaData.put("name", getProjectName());
        properties.setMetadata(metaData);
        return new NacosWatch(properties);
    }



}
