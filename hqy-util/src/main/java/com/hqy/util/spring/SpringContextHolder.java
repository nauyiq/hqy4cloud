package com.hqy.util.spring;


import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author qy
 * @create 2021-07-22 16:25
 **/
@Component
public class SpringContextHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private static ProjectContextInfo contextInfo = new ProjectContextInfo();

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        SpringContextHolder.applicationContext = context; // NOSONAR
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
        return (T) applicationContext.getBean(clazz);
    }

    public static <T> T getBean(Class<T> clazz, T defaultObjWhenException) {
        try {
            T x = getBean(clazz);
            if (x == null) {
                return defaultObjWhenException;
            } else {
                return x;
            }
        } catch (Exception e) {
            return defaultObjWhenException;
        }

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
            throw new IllegalStateException("applicaitonContext未注入,请在applicationContext.xml中定义SpringContextHolder");
        }
    }

    public static boolean isSpringApplicationContextOk() {
        return applicationContext != null;
    }


    /**
     * 注册上下文 Context信息，方便全局使用
     * @param name
     * @param envriment
     * @param createTime
     */
//	public static void registContextInfo(GfwContextInfo info) {
//		if(info != null){
//			congtextInfo = info;
//		}
//	}


    /**
     * * 往spring ioc容器动态注入一个bean
     *
     * @param beanId
     * @param clazz
     */
    public static void registDynamicBean(String beanId, Class<?> clazz) {

        // 获取BeanFactory
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) getApplicationContext()
                .getAutowireCapableBeanFactory();
        // 创建bean信息.
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
//        beanDefinitionBuilder.addPropertyValue("name", "张三");
        // 动态注册bean.
//        defaultListableBeanFactory.registerBeanDefinition("testService", beanDefinitionBuilder.getBeanDefinition());
        defaultListableBeanFactory.registerBeanDefinition(beanId, beanDefinitionBuilder.getBeanDefinition());
    }


	public static ProjectContextInfo getProjectContextInfo(){
		return contextInfo;
	}

    public static void registerContextInfo(ProjectContextInfo info) {
        if (Objects.nonNull(info)) {
            contextInfo = info;
        }
    }


    /**
     * 处理spring event
     * @param event
     */
    public static void publishEvent(ApplicationEvent event) {
        if (event != null && applicationContext != null) {
            try {
                ////获取父容器发送事件
                if (applicationContext.getParent() == null) {
                    //MVC 容器
                    applicationContext.publishEvent(event);
                } else {
                    //通过父容器来发送事件，防止mvc + spring 场景发送了两次
                    applicationContext.getParent().publishEvent(event);
                }
            } catch (Exception ex) {
                System.err.println("Error applicationContext.publishEvent: " + ex.getClass().getName() + ", " + ex.getMessage());
            }
        }
    }


}
