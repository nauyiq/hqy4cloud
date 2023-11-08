package com.hqy.cloud.canal.config;

import com.hqy.cloud.canal.core.CanalGlue;
import com.hqy.cloud.canal.core.DefaultCanalBinlogEventProcessorFactory;
import com.hqy.cloud.canal.core.DefaultCanalGlue;
import com.hqy.cloud.canal.core.parser.BaseParseResultInterceptor;
import com.hqy.cloud.canal.core.parser.CanalBinLogEventParser;
import com.hqy.cloud.canal.core.parser.ModelTableMetadataManager;
import com.hqy.cloud.canal.core.parser.ParseResultInterceptorManager;
import com.hqy.cloud.canal.core.parser.converter.CanalFieldConverterFactory;
import com.hqy.cloud.canal.core.parser.converter.support.InMemoryCanalFieldConverterFactory;
import com.hqy.cloud.canal.core.parser.support.DefaultCanalBinLogEventParser;
import com.hqy.cloud.canal.core.parser.support.InMemoryModelTableMetadataManager;
import com.hqy.cloud.canal.core.parser.support.InMemoryParseResultInterceptorManager;
import com.hqy.cloud.canal.core.processor.BaseCanalBinlogEventProcessor;
import com.hqy.cloud.canal.core.processor.CanalBinlogEventProcessorFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Map;

/**
 * CanalAutoConfiguration.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/18 10:15
 */
@Configuration
public class CanalAutoConfiguration implements SmartInitializingSingleton, BeanFactoryAware {
    private ConfigurableListableBeanFactory configurableListableBeanFactory;

    public ConfigurableListableBeanFactory getConfigurableListableBeanFactory() {
        return configurableListableBeanFactory;
    }

    @Bean
    @ConditionalOnMissingBean
    public CanalBinlogEventProcessorFactory canalBinlogEventProcessorFactory() {
        return DefaultCanalBinlogEventProcessorFactory.of();
    }

    @Bean
    @ConditionalOnMissingBean
    public ModelTableMetadataManager modelTableMetadataManager(CanalFieldConverterFactory canalFieldConverterFactory) {
        return InMemoryModelTableMetadataManager.of(canalFieldConverterFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public CanalFieldConverterFactory canalFieldConverterFactory() {
        return InMemoryCanalFieldConverterFactory.of();
    }

    @Bean
    @ConditionalOnMissingBean
    public CanalBinLogEventParser canalBinLogEventParser() {
        return DefaultCanalBinLogEventParser.of();
    }

    @Bean
    @ConditionalOnMissingBean
    public ParseResultInterceptorManager parseResultInterceptorManager(ModelTableMetadataManager modelTableMetadataManager) {
        return InMemoryParseResultInterceptorManager.of(modelTableMetadataManager);
    }

    @Bean
    @Primary
    public CanalGlue canalGlue(CanalBinlogEventProcessorFactory canalBinlogEventProcessorFactory) {
        return DefaultCanalGlue.of(canalBinlogEventProcessorFactory);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.configurableListableBeanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void afterSingletonsInstantiated() {
        ParseResultInterceptorManager parseResultInterceptorManager
                = configurableListableBeanFactory.getBean(ParseResultInterceptorManager.class);
        ModelTableMetadataManager modelTableMetadataManager
                = configurableListableBeanFactory.getBean(ModelTableMetadataManager.class);
        CanalBinlogEventProcessorFactory canalBinlogEventProcessorFactory
                = configurableListableBeanFactory.getBean(CanalBinlogEventProcessorFactory.class);
        CanalBinLogEventParser canalBinLogEventParser
                = configurableListableBeanFactory.getBean(CanalBinLogEventParser.class);
        Map<String, BaseParseResultInterceptor> interceptors
                = configurableListableBeanFactory.getBeansOfType(BaseParseResultInterceptor.class);
        interceptors.forEach((k, interceptor) -> parseResultInterceptorManager.registerParseResultInterceptor(interceptor));
        Map<String, BaseCanalBinlogEventProcessor> processors
                = configurableListableBeanFactory.getBeansOfType(BaseCanalBinlogEventProcessor.class);
        processors.forEach((k, processor) -> processor.init(canalBinLogEventParser, modelTableMetadataManager,
                canalBinlogEventProcessorFactory, parseResultInterceptorManager));
    }
}
