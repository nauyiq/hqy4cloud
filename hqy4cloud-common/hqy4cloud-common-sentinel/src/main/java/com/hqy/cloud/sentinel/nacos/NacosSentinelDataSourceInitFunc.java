package com.hqy.cloud.sentinel.nacos;

import cn.hutool.core.util.StrUtil;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.hqy.cloud.rpc.nacos.node.NacosServerInfo;
import com.hqy.cloud.util.spring.ProjectContextInfo;
import com.hqy.cloud.util.spring.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * NacosSentinelDataSourceInitFunc
 * nacos sentinel配置加载到sentinel dashboard.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/6 18:16
 */
@Slf4j
public class NacosSentinelDataSourceInitFunc implements InitFunc {

    @Override
    public void init() throws Exception {
        NacosServerInfo nacosServerInfo = ProjectContextInfo.getBean(NacosServerInfo.class);
        ProjectContextInfo projectContextInfo = SpringContextHolder.getProjectContextInfo();
        if (Objects.isNull(nacosServerInfo) || Objects.isNull(projectContextInfo)) {
            log.warn("Failed execute to register nacos datasource, this service not registry to nacos.");
            return;
        }
        String dataId = projectContextInfo.getNameEn() + StrUtil.DASHED + "flow";
        Properties properties = new Properties();
        properties.setProperty("serverAddr", nacosServerInfo.getServerAddr());
        properties.setProperty("namespace", nacosServerInfo.getNamespace());
        ReadableDataSource<String, List<FlowRule>> flowRuleDatasource = new NacosDataSource<>(properties,
                nacosServerInfo.getGroup(), dataId, source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>(){}));
        FlowRuleManager.register2Property(flowRuleDatasource.getProperty());
    }
}
