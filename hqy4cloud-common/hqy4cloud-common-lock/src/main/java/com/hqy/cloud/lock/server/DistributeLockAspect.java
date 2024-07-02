package com.hqy.cloud.lock.server;

import com.hqy.cloud.cache.common.RedisConstants;
import com.hqy.cloud.lock.annotation.DistributeLock;
import com.hqy.cloud.lock.common.DistributeLockException;
import com.hqy.cloud.lock.common.LockConstants;
import com.hqy.cloud.lock.service.LockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁切面
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/18
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DistributeLockAspect {
    private final LockService lockService;

    /**
     * 切点
     */
    @Pointcut("@annotation(com.hqy.cloud.lock.annotation.DistributeLock)")
    private void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        DistributeLock distributeLock = method.getAnnotation(DistributeLock.class);

        String key = distributeLock.key();
        if (StringUtils.isBlank(key) || LockConstants.NONE_KEY.equals(key)) {
            String keyExpression = distributeLock.keyExpression();
            // 判断表达式是否存在
            if (StringUtils.isBlank(keyExpression) || LockConstants.NONE_KEY.equals(keyExpression)) {
                throw new DistributeLockException("Not found distribute lock key.");
            }
            // 解析spel表达式
            SpelExpressionParser parser = new SpelExpressionParser();
            Expression expression = parser.parseExpression(keyExpression);
            EvaluationContext context = new StandardEvaluationContext();

            Object[] args = point.getArgs();
            // 获取运行时参数的名称
            StandardReflectionParameterNameDiscoverer discoverer
                    = new StandardReflectionParameterNameDiscoverer();
            String[] parameterNames = discoverer.getParameterNames(method);
            // 将参数绑定到context中
            if (parameterNames != null) {
                for (int i = 0; i < parameterNames.length; i++) {
                    context.setVariable(parameterNames[i], args[i]);
                }
            }
            // 解析表达式，获取结果
            key = String.valueOf(expression.getValue(context));
        }

        String lock = distributeLock.scene() + RedisConstants.CACHE_KEY_SEPARATOR + key;
        int waitTime = distributeLock.waitTime();
        int expired = distributeLock.expireTime();
        boolean result;
        // do lock
        if (waitTime == LockConstants.DEFAULT_WAIT_TIME) {
            if (expired == LockConstants.DEFAULT_EXPIRE_TIME) {
                log.info("Distribute lock for key: {}", lock);
                lockService.lock(lock);
            } else {
                log.info("Distribute lock for key: {}, expired: {}", lock, expired);
                lockService.lock(lock, expired, TimeUnit.MILLISECONDS);
            }
            result = true;
        } else {
           if (expired == LockConstants.DEFAULT_EXPIRE_TIME) {
               log.info("Distribute try lock for key: {}, waitTime: {}", lock, waitTime);
               result = lockService.tryLock(lock, waitTime, TimeUnit.MILLISECONDS);
           } else {
               log.info("Distribute try lock for key: {}, waitTime: {}, expired: {}", lock, waitTime, expired);
               result = lockService.tryLock(lock, expired, waitTime, TimeUnit.MILLISECONDS);
           }
        }

        if (!result) {
            log.warn("Failed execute to do lock for key: {}, waitTime: {}", lock, waitTime);
            throw new DistributeLockException("Failed execute to do lock for key: " + lock);
        }

        Object response;
        try {
            response = point.proceed();
            if (log.isDebugEnabled()) {
                log.debug("Distribute lock success for key : {}", lock);
            }
        } finally {
            lockService.unlock(lock);
            if (log.isDebugEnabled()) {
                log.debug("Distribute unlock for key: {}", lock);
            }
        }
        return response;
    }

}
