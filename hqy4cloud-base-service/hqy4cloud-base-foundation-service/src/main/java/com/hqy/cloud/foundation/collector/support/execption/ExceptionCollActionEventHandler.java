package com.hqy.cloud.foundation.collector.support.execption;

import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.common.swticher.ServerSwitcher;
import com.hqy.cloud.util.JsonUtil;
import com.hqy.cloud.util.spring.SpringContextHolder;
import com.hqy.foundation.spring.event.ExceptionCollActionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 监听ExceptionCollActionEvent
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/7 14:47
 */
@Slf4j
@RefreshScope
public class ExceptionCollActionEventHandler {
    private static final Set<String> IGNORE_EXCEPTION_CLASS_LIST = new HashSet<>();

    @Value("exception.ignore.class:''")
    private Set<String> ignoreExceptionClassFromConfig;

    static {
        IGNORE_EXCEPTION_CLASS_LIST.add("org.springframework.security.web.authentication.rememberme.CookieTheftException");
        IGNORE_EXCEPTION_CLASS_LIST.add("javax.servlet.ServletException");
        IGNORE_EXCEPTION_CLASS_LIST.add("com.stripe.exception.CardException");
        IGNORE_EXCEPTION_CLASS_LIST.add("org.apache.catalina.connector.ClientAbortException");
        IGNORE_EXCEPTION_CLASS_LIST.add("org.springframework.security.authentication.BadCredentialsException");
        IGNORE_EXCEPTION_CLASS_LIST.add("org.springframework.web.multipart.MultipartException");
    }

    @Async
    @EventListener(classes = ExceptionCollActionEvent.class)
    public void eventListener(ExceptionCollActionEvent event) {
        if (Objects.isNull(event) || Objects.isNull(event.getSource())) {
            log.warn("[ExceptionCollActionEventHandler] event or event.source is null.");
            return;
        }
        if (CommonSwitcher.ENABLE_EXCEPTION_COLLECTOR.isOff()) {
            // 采集开关没有打开
            return;
        }
        // 判断是否需要采集'采集服务'的异常
        if (SpringContextHolder.getProjectContextInfo().getNameEn().equals(MicroServiceConstants.COMMON_COLLECTOR) &&
                ServerSwitcher.ENABLE_COLLECT_COLLECTION_SERVICE_EXCEPTION.isOff()) {
            return;
        }

        Throwable exception = event.getException();
        if (ignoreException(exception)) {
            // 忽略采集的异常
            log.warn("[ExceptionCollActionEventHandler] ignoreException: {}", exception.getClass().getName());
            return;
        }
        try {
            ExceptionCollectorUtils.collect(event);
        } catch (Exception e) {
            log.error("[ExceptionCollActionEventHandler] doCollection error, event:{}", JsonUtil.toJson(event));
            log.error(e.getMessage(), e);
        }
    }


    private boolean ignoreException(Throwable exception) {
        String exceptionName = exception.getClass().getName();
        return IGNORE_EXCEPTION_CLASS_LIST.stream().anyMatch(clazz -> clazz.equals(exceptionName)) || ignoreExceptionClassFromConfig.stream().anyMatch(clazz -> clazz.equals(exceptionName));
    }



}
