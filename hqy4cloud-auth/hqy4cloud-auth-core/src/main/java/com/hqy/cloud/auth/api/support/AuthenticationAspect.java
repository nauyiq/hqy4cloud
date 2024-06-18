package com.hqy.cloud.auth.api.support;

import com.hqy.cloud.auth.annotation.PreAuthentication;
import com.hqy.cloud.auth.api.AuthPermissionService;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/8 13:44
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class AuthenticationAspect {
    private final AuthPermissionService authPermissionService;

    @Pointcut("(execution(public * *(..)) && @annotation(org.springframework.web.bind.annotation.RequestMapping) && @annotation(com.hqy.cloud.auth.annotation.PreAuthentication)) || "
            + "(execution(public * *(..)) && @annotation(org.springframework.web.bind.annotation.GetMapping) && @annotation(com.hqy.cloud.auth.annotation.PreAuthentication)) || "
            + "(execution(public * *(..)) && @annotation(org.springframework.web.bind.annotation.PostMapping) && @annotation(com.hqy.cloud.auth.annotation.PreAuthentication)) || "
            + "(execution(public * *(..)) && @annotation(org.springframework.web.bind.annotation.PutMapping) && @annotation(com.hqy.cloud.auth.annotation.PreAuthentication)) || "
            + "(execution(public * *(..)) && @annotation(org.springframework.web.bind.annotation.DeleteMapping) && @annotation(com.hqy.cloud.auth.annotation.PreAuthentication)) ")
    private void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        PreAuthentication authentication = getAuthentication(pjp);
        if (Objects.isNull(authentication)) {
            return pjp.proceed();
        }
        // 判断是否有权限.
        if (authPermissionService.hasAuthorities(authentication.value())) {
            return pjp.proceed();
        }
        return R.failed(ResultCode.NOT_PERMISSION);
    }


    private PreAuthentication getAuthentication(JoinPoint point) {
        try {
            Method method = ((MethodSignature) point.getSignature()).getMethod();
            PreAuthentication annotation = method.getAnnotation(PreAuthentication.class);
            if (Objects.isNull(annotation)) {
                log.warn("Failed execute to do annotation for around aspect, not found annotation.");
            }
            return annotation;
        } catch (Throwable cause) {
            log.error("Failed execute to get Logging annotation, message: {}.", cause.getMessage(), cause);
            return null;
        }
    }






}
