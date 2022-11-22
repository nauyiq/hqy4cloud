package com.hqy.web.service.collect;

import com.hqy.foundation.spring.event.ExceptionCollActionEvent;
import com.hqy.base.common.swticher.CommonSwitcher;
import com.hqy.foundation.common.enums.ExceptionLevel;
import com.hqy.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 监听ExceptionCollActionEvent
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/7 14:47
 */
@Lazy
@Component
public class ExceptionCollActionEventHandler {

    private static final Logger log = LoggerFactory.getLogger(ExceptionCollActionEventHandler.class);

    /**
     * 频率计数器
     */
    private final Map<Integer, Long> counter = new ConcurrentHashMap<>();

    /**
     * 最大并发
     */
    private static final int MAX_CONCURRENT = 8;

    /**
     * 可重入锁 个数
     */
    private final Lock[] locks = new ReentrantLock[MAX_CONCURRENT];


    private static final Set<String> IGNORE_EXCEPTION_CLASS_LIST = new HashSet<>();


    private final AtomicInteger concurrent = new AtomicInteger(0);


    public ExceptionCollActionEventHandler() {
        for (int i = 0; i < MAX_CONCURRENT; i++) {
            locks[i] = new ReentrantLock(true);
        }
    }

    static {
        IGNORE_EXCEPTION_CLASS_LIST.add("org.springframework.security.web.authentication.rememberme.CookieTheftException");
        IGNORE_EXCEPTION_CLASS_LIST.add("javax.servlet.ServletException");
        IGNORE_EXCEPTION_CLASS_LIST.add("com.stripe.exception.CardException");
        IGNORE_EXCEPTION_CLASS_LIST.add("org.apache.catalina.connector.ClientAbortException");
        IGNORE_EXCEPTION_CLASS_LIST.add("org.springframework.security.authentication.BadCredentialsException");
        IGNORE_EXCEPTION_CLASS_LIST.add("org.springframework.web.multipart.MultipartException");
    }


    @Async
    @EventListener
    public void eventListener(ExceptionCollActionEvent event) {
        if (Objects.isNull(event) || Objects.isNull(event.getSource())) {
            log.warn("[ExceptionCollActionEventHandler] event or event.source is null.");
            return;
        }
        Throwable exception = event.getException();
        if (ignoreException(exception)) {
            log.warn("[ExceptionCollActionEventHandler] ignoreException: {}", exception.getClass().getName());
            return;
        }

        log.info("[ExceptionCollActionEventHandler] {}, {}, {}.",
                event.getSource(), exception.getClass(), exception.getMessage());

        if (CommonSwitcher.ENABLE_EXCEPTION_COLL_ACTION_EVENT_HANDLER.isOff()) {
            log.info("[ExceptionCollActionEventHandler] CommonSwitcher.ENABLE_EXCEPTION_COLL_ACTION_EVENT_HANDLER.isOff().");
            return;
        }
        if (concurrent.get() >= MAX_CONCURRENT) {
            log.warn("[ExceptionCollActionEventHandler] has MAX_CONCURRENT = {}, msg:{}", MAX_CONCURRENT, exception.getMessage());
            return;
        }

        int hashCode = event.hashCode();
        int sign = Math.abs(hashCode) % MAX_CONCURRENT;
        Long count = counter.get(hashCode);
        count = count == null ? 0 : count;
        Lock lock = locks[sign];
        lock.lock();
        try {
            doCollection(event, count);
        } catch (Exception e) {
            log.error("[ExceptionCollActionEventHandler] doCollection error, event:{}", JsonUtil.toJson(event));
            log.error(e.getMessage(), e);
        } finally {
            lock.unlock();
            if (count > Integer.MAX_VALUE) {
                count = 0L;
            }
            counter.put(hashCode, ++count);
        }

    }

    private void doCollection(ExceptionCollActionEvent event, Long count) {
        concurrent.incrementAndGet();
        try {
            boolean flag = !event.isFilter() || count % event.getStep() == 0;
            if (flag) {
                ExceptionCollectorUtils.collect(event.getException(), event.getParam(), event.getResultCode(), ExceptionLevel.WARN);
            }
        } catch (Exception e) {
            log.warn("[ExceptionCollActionEventHandler] doCollection fail.");
        } finally {
            concurrent.decrementAndGet();
        }
    }


    private boolean ignoreException(Throwable exception) {
        String exceptionName = exception.getClass().getName();
        return IGNORE_EXCEPTION_CLASS_LIST.stream().anyMatch(clazz -> clazz.equals(exceptionName));
    }



}
