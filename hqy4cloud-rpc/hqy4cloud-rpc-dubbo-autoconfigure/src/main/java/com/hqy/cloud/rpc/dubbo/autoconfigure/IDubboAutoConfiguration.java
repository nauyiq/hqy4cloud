package com.hqy.cloud.rpc.dubbo.autoconfigure;

import com.hqy.cloud.registry.common.model.ProjectInfoModel;
import com.hqy.cloud.rpc.dubbo.deploy.DubboDeployModel;
import com.hqy.cloud.rpc.dubbo.facade.FacadeAspect;
import com.hqy.cloud.util.config.YamlPropertySourceFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import static com.hqy.cloud.registry.common.Constants.CONFIGURATION_PREFIX_COMPONENTS;

/**
 * dubbo启动配置类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/9
 */
@Configuration
@PropertySource(value = "classpath:dubbo.yml", factory = YamlPropertySourceFactory.class)
public class IDubboAutoConfiguration {

    @Bean
    public FacadeAspect facadeAspect() {
        return new FacadeAspect();
    }


    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = CONFIGURATION_PREFIX_COMPONENTS, name = "dubbo.enabled", havingValue = "true")
    public DubboDeployModel dubboProviderDeployModel(ProjectInfoModel projectInfoModel) {
        return new DubboDeployModel(projectInfoModel);
    }
}
