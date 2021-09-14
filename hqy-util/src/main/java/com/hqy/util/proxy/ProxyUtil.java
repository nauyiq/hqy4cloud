package com.hqy.util.proxy;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-09-13 17:11
 */
public class ProxyUtil {

    public static Object getTarget(Object proxy) throws Exception {
        if (Proxy.isProxyClass(proxy.getClass())) {
            return getJdkDynamicProxyTargetObject(proxy);
        } else if(ClassUtils.isCglibProxy(proxy.getClass())){ // cglib
            return getCglibProxyTargetObject(proxy);
        }else{
            return proxy;
        }
    }

    private static Object getCglibProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
        h.setAccessible(true);
        Object dynamicAdvisedInterceptor = h.get(proxy);
        Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        return ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();
    }

    private static Object getJdkDynamicProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getDeclaredField("h");
        h.setAccessible(true);
        AopProxy aopProxy = (AopProxy) h.get(proxy);
        Field advised = aopProxy.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        return ((AdvisedSupport) advised.get(aopProxy)).getTargetSource().getTarget();
    }
}
