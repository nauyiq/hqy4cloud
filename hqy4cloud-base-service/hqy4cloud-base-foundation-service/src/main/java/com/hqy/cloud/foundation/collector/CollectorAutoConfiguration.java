package com.hqy.cloud.foundation.collector;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.foundation.collector.support.CollectorCenter;
import com.hqy.cloud.foundation.collector.support.execption.ExceptionCollActionEventHandler;
import com.hqy.cloud.foundation.collector.support.execption.ExceptionCollector;
import com.hqy.cloud.foundation.collector.support.sql.SqlCollector;
import com.hqy.cloud.foundation.collector.support.throttles.ThrottledCollector;
import com.hqy.foundation.common.EventType;
import com.hqy.foundation.collection.Collector;
import com.hqy.foundation.collection.CollectorConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;

/**
 * 采集器配置类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/12 17:35
 */
@Configuration
public class CollectorAutoConfiguration implements SmartInitializingSingleton, BeanFactoryAware {
    private ConfigurableListableBeanFactory configurableListableBeanFactory;

    @Bean
    public ExceptionCollActionEventHandler handler() {
        return new ExceptionCollActionEventHandler();
    }

    @Bean
    public ExceptionCollector exceptionCollector() {
        return new ExceptionCollector();
    }

    @Bean
    public SqlCollector sqlCollector() {
        return new SqlCollector();
    }

    @Bean
    public ThrottledCollector throttledCollector() {
        return new ThrottledCollector();
    }


    @Override
    public void setBeanFactory(@Nonnull BeanFactory beanFactory) throws BeansException {
        this.configurableListableBeanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void afterSingletonsInstantiated() {
        Map<String, Collector> map = this.configurableListableBeanFactory.getBeansOfType(Collector.class);
        if (MapUtil.isNotEmpty(map)) {
            Collection<Collector> collectors = map.values();
            collectors.forEach(collector -> {
                EventType type = collector.type();
                CollectorConfig config = CollectorCenter.getInstance().getConfig(type);
                collector.setConfig(config);
                CollectorCenter.getInstance().registry(collector);
            });
        }
    }
}
