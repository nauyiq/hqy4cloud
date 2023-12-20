package com.hqy.cloud.foundation.event;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.foundation.event.alerter.AlerterHolder;
import com.hqy.cloud.foundation.event.alerter.DefaultAlerter;
import com.hqy.cloud.foundation.event.alerter.NotificationHolder;
import com.hqy.cloud.foundation.event.collector.support.CollectorCenter;
import com.hqy.cloud.foundation.event.collector.support.execption.ExceptionCollActionEventHandler;
import com.hqy.cloud.foundation.event.collector.support.execption.ExceptionCollector;
import com.hqy.cloud.foundation.event.collector.support.sql.SqlCollector;
import com.hqy.cloud.foundation.event.collector.support.throttles.ThrottledCollector;
import com.hqy.foundation.common.EventType;
import com.hqy.foundation.event.collection.Collector;
import com.hqy.foundation.event.collection.CollectorConfig;
import com.hqy.foundation.event.notice.Notifier;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/12 17:35
 */
@Configuration
public class EventAutoConfiguration implements SmartInitializingSingleton, BeanFactoryAware {
    private ConfigurableListableBeanFactory configurableListableBeanFactory;

    @Bean
    @ConditionalOnMissingBean
    public AlerterHolder alerterHolder() {
        return new AlerterHolder(new DefaultAlerter());
    }

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
        // 注册采集器
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
        // 注册通知器
        Map<String, Notifier> notifierMap = this.configurableListableBeanFactory.getBeansOfType(Notifier.class);
        if (MapUtil.isNotEmpty(notifierMap)) {
            notifierMap.values().forEach(notifier -> NotificationHolder.registryNotifier(notifier.type(), notifier));
        }
    }
}
