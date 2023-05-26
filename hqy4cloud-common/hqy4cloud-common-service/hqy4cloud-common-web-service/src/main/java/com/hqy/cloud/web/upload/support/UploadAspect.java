package com.hqy.cloud.web.upload.support;

import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.web.common.annotation.UploadMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 文件上传切面.
 * 将文件上传的请求方式注入到上下文中
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/25 17:02
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class UploadAspect {

    @Pointcut("(execution(public * *(..)) && @annotation(org.springframework.web.bind.annotation.RequestMapping) && @annotation(com.hqy.cloud.web.common.annotation.UploadMode)) || "
            + "(execution(public * *(..)) && @annotation(org.springframework.web.bind.annotation.PostMapping) && @annotation(com.hqy.cloud.web.common.annotation.UploadMode)) ")
    private void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        UploadMode uploadMode = getUploadMode(pjp);
        if (Objects.nonNull(uploadMode)) {
            UploadContext.setMode(uploadMode.value());
        }
        try {
            return pjp.proceed();
        } finally {
            UploadContext.removeMode();
        }
    }

    private UploadMode getUploadMode(ProceedingJoinPoint pjp) {
        try {
            Method method = ((MethodSignature) pjp.getSignature()).getMethod();
            UploadMode annotation = method.getAnnotation(UploadMode.class);
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
