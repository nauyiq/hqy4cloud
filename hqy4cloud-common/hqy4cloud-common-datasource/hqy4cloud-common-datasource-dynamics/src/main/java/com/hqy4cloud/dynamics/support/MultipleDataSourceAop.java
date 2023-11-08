package com.hqy4cloud.dynamics.support;

import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.util.proxy.ProxyUtil;
import com.hqy4cloud.dynamics.annotation.DataSourceType;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * 多数据源自动切换通知类<br>
 * @author qy
 * @date  2021-08-09 18:25
 */
@Aspect
public class MultipleDataSourceAop {

    private static final Logger log = LoggerFactory.getLogger(MultipleDataSourceAop.class);

    /**
     * 切点，service中的所有方法.
     */
    @Pointcut("execution(* com.hqy.cloud.*.service.*.*(..))")
    public void dataSourcePointCut() {
    }


    /**
     * 前置通知, 动态切换数据源
     * @param joinPoint
     * @throws Throwable
     */
    @Around("dataSourcePointCut()")
    public Object dynamicDataSource(ProceedingJoinPoint joinPoint) throws Throwable {
        // 拦截的实体类，就是当前正在执行的service
        Object target = ProxyUtil.getTarget(joinPoint.getTarget());
        if (target.getClass().isAnnotationPresent(DataSourceType.class)) {
            DataSourceType dataSourceType = target.getClass().getAnnotation(DataSourceType.class);
            String type = dataSourceType.value();
            DynamicMultipleDataSourceContextHolder.setDataSourceName(type);
            try {
                return joinPoint.proceed();
            } finally {
                DynamicMultipleDataSourceContextHolder.clearDataSourceName();
            }
        }

        //检查方法签名，是否有注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Method m = target.getClass().getMethod(method.getName());
        if (m.isAnnotationPresent(DataSourceType.class)) {
            DataSourceType dataSourceType = m.getAnnotation(DataSourceType.class);
            String type = dataSourceType.value();
            DynamicMultipleDataSourceContextHolder.setDataSourceName(type);
            try {
                return joinPoint.proceed();
            } finally {
                DynamicMultipleDataSourceContextHolder.clearDataSourceName();
            }
        }

        if (log.isDebugEnabled() && CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
            log.debug("此{}不涉及数据源操作.", target.getClass());
        }
        return joinPoint.proceed();
    }


}
