package com.hqy.util.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;

/**
 * SpringContextHolder bean 需要优先加载
 * 因此理由到bean实例化之前 会先检测是否存在InstantiationAwareBeanPostProcessor接口
 * https://blog.csdn.net/liuyueyi25/article/details/104970404
 * TODO 缺点是存在代码侵入， 后续再找其他方案进行代替...
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/1 13:36
 */
@Slf4j
public class SpringContextHolderProcessor extends InstantiationAwareBeanPostProcessorAdapter implements BeanFactoryAware {

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
            throw new IllegalArgumentException(
                    "AutowiredAnnotationBeanPostProcessor requires a ConfigurableListableBeanFactory: " + beanFactory);
        }

        ConfigurableListableBeanFactory configurableListableBeanFactory = (ConfigurableListableBeanFactory) beanFactory;
        // 通过主动调用beanFactory#getBean来显示实例化目标bean
        SpringContextHolder bean = configurableListableBeanFactory.getBean(SpringContextHolder.class);

        log.info("@@@ SpringContextHolderProcessor create bean success, bean:{}", bean);
    }
}
