package com.hqy.cloud.sentinel.nacos;

import cn.hutool.core.util.StrUtil;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.hqy.cloud.registry.common.context.BeanRepository;
import com.hqy.cloud.registry.common.model.ApplicationModel;
import com.hqy.cloud.registry.common.model.RegistryInfo;
import com.hqy.cloud.registry.context.ProjectContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
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
        ProjectContext context = BeanRepository.getInstance().getBean(ProjectContext.class);
        if (context != null) {
            ApplicationModel model = context.registryContext().getModel();
            RegistryInfo registryInfo = model.getRegistryInfo();
            String dataId = model.getApplicationName() + StrUtil.DASHED + "flow";
            Properties properties = new Properties();
            properties.setProperty(PropertyKeyConst.SERVER_ADDR, registryInfo.getAddress());
            properties.setProperty(PropertyKeyConst.NAMESPACE, model.getNamespace());
            properties.setProperty(PropertyKeyConst.USERNAME, registryInfo.getUsername());
            properties.setProperty(PropertyKeyConst.PASSWORD, registryInfo.getPassword());
            ReadableDataSource<String, List<FlowRule>> flowRuleDatasource = new NacosDataSource<>(properties,
                    model.getGroup(), dataId, source -> JSON.parseObject(source, new TypeReference<>() {}));
            FlowRuleManager.register2Property(flowRuleDatasource.getProperty());
        } else {
            log.warn("Failed execute to register nacos datasource, this service not registry to nacos.");
        }




    }
}
