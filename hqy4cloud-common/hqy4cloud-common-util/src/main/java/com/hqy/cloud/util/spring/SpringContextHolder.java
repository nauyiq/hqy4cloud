package com.hqy.cloud.util.spring;


import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ArrayUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.ResolvableType;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * spring容器加强类
 * @author qiyuan.hong
 * @date 2021-07-22 16:25
 **/
@Slf4j
public class SpringContextHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private volatile static ProjectContextInfo contextInfo;


    @Override
    @SneakyThrows
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        SpringContextHolder.applicationContext = context;
    }

    /**
     * 取得存储在静态变量中的ApplicationContext.
     */
    public static ApplicationContext getApplicationContext() {
        checkApplicationContext();
        return applicationContext;
    }

    /**
     * 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        checkApplicationContext();
        return (T) applicationContext.getBean(name);
    }

    /**
     * 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    public static <T> T getBean(Class<T> clazz) {
        checkApplicationContext();
        return applicationContext.getBean(clazz);
    }

    public static <T> T getBean(Class<T> clazz, T defaultObjWhenException) {
        try {
            return getBean(clazz);
        } catch (Exception e) {
            return defaultObjWhenException;
        }

    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(TypeReference<T> reference) {
        final ParameterizedType parameterizedType = (ParameterizedType) reference.getType();
        final Class<T> rawType = (Class<T>) parameterizedType.getRawType();
        final Class<?>[] genericTypes = Arrays.stream(parameterizedType.getActualTypeArguments()).map(type -> (Class<?>) type).toArray(Class[]::new);
        final String[] beanNames = applicationContext.getBeanNamesForType(ResolvableType.forClassWithGenerics(rawType, genericTypes));
        return getBean(beanNames[0], rawType);
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> type) {
        return applicationContext.getBeansOfType(type);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return applicationContext.getBean(name, clazz);
    }

    /**
     * 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    public static <T> T getBean(Class<T> clazz, String beanid) {
        checkApplicationContext();
        return (T) applicationContext.getBean(beanid, clazz);
    }


    /**
     * 清除applicationContext静态变量.
     */
    public static void cleanApplicationContext() {
        applicationContext = null;
    }

    private static void checkApplicationContext() {
        if (applicationContext == null) {
            throw new IllegalStateException("applicationContext未注入.");
        }
    }

    public static boolean isSpringApplicationContextOk() {
        return applicationContext != null;
    }



    /**
     * * 往spring ioc容器动态注入一个bean
     * @param beanId
     * @param clazz
     */
    public static void registryDynamicBean(String beanId, Class<?> clazz) {
        // 获取BeanFactory
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) getApplicationContext()
                .getAutowireCapableBeanFactory();
        // 创建bean信息.
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        // 动态注册bean.
        defaultListableBeanFactory.registerBeanDefinition(beanId, beanDefinitionBuilder.getBeanDefinition());
    }


    public static void registerContextInfo(ProjectContextInfo info) {
        if (Objects.nonNull(info)) {
            contextInfo = info;
        }
    }

    public static String[] getActiveProfiles() {
        return applicationContext.getEnvironment().getActiveProfiles();
    }

    public static String getActiveProfile() {
        final String[] activeProfiles = getActiveProfiles();
        return ArrayUtil.isNotEmpty(activeProfiles) ? activeProfiles[0] : null;
    }

    public static <T> void registerBean(String beanName, T bean) {
        ConfigurableApplicationContext context = (ConfigurableApplicationContext) applicationContext;
        context.getBeanFactory().registerSingleton(beanName, bean);
    }



    /**
     * 处理spring event
     * @param event
     */
    public static void publishEvent(ApplicationEvent event) {
        if (event != null && applicationContext != null) {
            try {
//                ApplicationEventPublisher applicationEventPublisher = applicationContext.getBean(ApplicationEventPublisher.class);
                applicationContext.publishEvent(event);
                ////获取父容器发送事件
                /*if (applicationContext.getParent() == null) {
                    //MVC 容器
                    applicationContext.publishEvent(event);
                } else {
                    //通过父容器来发送事件，防止mvc + spring 场景发送了两次
                    applicationContext.getParent().publishEvent(event);
                }*/
            } catch (Exception ex) {
                log.error("Error applicationContext.publishEvent: " + ex.getClass().getName() + ", " + ex.getMessage());
            }
        }
    }

}
