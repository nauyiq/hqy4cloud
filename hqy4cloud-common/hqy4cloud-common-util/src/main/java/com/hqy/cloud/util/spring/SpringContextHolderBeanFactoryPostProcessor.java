package com.hqy.cloud.util.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import javax.annotation.Nonnull;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/4/19
 */
@Slf4j
public class SpringContextHolderBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(@Nonnull ConfigurableListableBeanFactory beanFactory) throws BeansException {
        SpringContextHolder bean = beanFactory.getBean(SpringContextHolder.class);
        log.info(bean.toString());
    }
}
