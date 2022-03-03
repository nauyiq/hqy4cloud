package com.hqy.multiple.aop;

import com.hqy.multiple.DynamicMultipleDataSource;
import com.hqy.util.proxy.ProxyUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 多数据源自动切换通知类<br>
 * @author qy
 * @date  2021-08-09 18:25
 */
@Aspect
@Order(0)
@Component
public class MultipleDataSourceAop {

    private static final Logger log = LoggerFactory.getLogger(MultipleDataSourceAop.class);

    /**
     * 切点，service中的所有方法.
     */
    @Pointcut("execution(* com.hqy.*.service.*.*(..))")
    public void dataSourcePointCut() {
    }


    /**
     * 前置通知, 动态切换数据源
     * @param joinPoint
     * @throws Throwable
     */
    @Before("dataSourcePointCut()")
    public void dynamicDataSource(JoinPoint joinPoint) throws Throwable {
        // 拦截的实体类，就是当前正在执行的service
        Object target = ProxyUtil.getTarget(joinPoint.getTarget());
        if (target.getClass().isAnnotationPresent(DataSourceType.class)) {
            DataSourceType dataSourceType = target.getClass().getAnnotation(DataSourceType.class);
            String type = dataSourceType.value();
            log.debug("### 数据源切换至--->{}", type);
            DynamicMultipleDataSource.setDataSourceKey(type);
            return;
        }
        //检查方法签名，是否有注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Method m = target.getClass().getMethod(method.getName());

        if (m.isAnnotationPresent(DataSourceType.class)) {
            DataSourceType dataSourceType = m.getAnnotation(DataSourceType.class);
            String type = dataSourceType.value();
            log.debug("### 数据源切换至 ---> {}", type);
            DynamicMultipleDataSource.setDataSourceKey(type);
            return;
        }

        log.debug("此{}不涉及数据源操作.", target.getClass());
    }

    /**
     * 方法结束后，移除动态数据源
     * @throws Throwable
     */
    @Before("dataSourcePointCut()")
    public void afterReturning() throws Throwable {
        try {
            DynamicMultipleDataSource.clearDataSource();
            log.debug("### 数据源已移除！");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("### 数据源移除报错！");
        }
    }


}
