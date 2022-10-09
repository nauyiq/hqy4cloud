package com.hqy.web.service.collect;

import com.hqy.base.common.result.CommonResultCode;
import com.hqy.coll.service.ExceptionCollectionService;
import com.hqy.foundation.common.enums.ExceptionLevel;
import com.hqy.rpc.common.config.EnvironmentConfig;
import com.hqy.rpc.nacos.client.starter.RPCClient;
import com.hqy.util.spring.SpringContextHolder;
import com.hqy.util.thread.ExecutorServiceProject;
import com.hqy.util.thread.ParentExecutorService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/7 15:40
 */
public class ExceptionCollectorUtils {

    private static final Logger log = LoggerFactory.getLogger(ExceptionCollectorUtils.class);

    private ExceptionCollectorUtils() {}

    private static final Set<String> IGNORE_EX_STACK_PACKAGE = new HashSet<>();

    static {
        IGNORE_EX_STACK_PACKAGE.add("org.springframework.web.method.support");
        IGNORE_EX_STACK_PACKAGE.add("org.apache.tomcat.util.threads");
        IGNORE_EX_STACK_PACKAGE.add("org.apache.coyote");
        IGNORE_EX_STACK_PACKAGE.add("org.apache.catalina");
        IGNORE_EX_STACK_PACKAGE.add("org.springframework.web.filter");
        IGNORE_EX_STACK_PACKAGE.add("org.springframework.web.servlet.mvc");
        IGNORE_EX_STACK_PACKAGE.add("org.springframework.security.web");
        IGNORE_EX_STACK_PACKAGE.add("org.springframework.beans");
        IGNORE_EX_STACK_PACKAGE.add("org.springframework.transaction");
        IGNORE_EX_STACK_PACKAGE.add("org.springframework.aop.framework");
        IGNORE_EX_STACK_PACKAGE.add("org.apache.tomcat.util.net");
        IGNORE_EX_STACK_PACKAGE.add("io.netty.channel");
        IGNORE_EX_STACK_PACKAGE.add("org.springframework.aop");
        IGNORE_EX_STACK_PACKAGE.add("org.hibernate");
        IGNORE_EX_STACK_PACKAGE.add("com.alibaba.druid");
        IGNORE_EX_STACK_PACKAGE.add("com.mysql.jdbc");
        IGNORE_EX_STACK_PACKAGE.add("io.netty.handler.codec.http.websocketx.CorruptedWebSocketFrameException");
    }


    /**
     * 异常采集 通过rpc发送
     * @param exception 异常
     * @param param 拓展参数
     * @param code 业务code
     */
    public static void collect(Throwable exception, String param, CommonResultCode code, ExceptionLevel exceptionLevel) {

        if (ParentExecutorService.getInstance().isQueueHundredthFull()) {
            //队列长度是否达到百分之一
            log.warn("@@@ ParentExecutorService当前比较繁忙, 此次异常不采集. exception:{}, param:{}, code:{}", exception, param, code);
            return;
        }

        ParentExecutorService.getInstance().execute(() -> {
            ExceptionCollectionService exceptionCollectionService = RPCClient.getRemoteService(ExceptionCollectionService.class);
            long now = System.currentTimeMillis();
            String exceptionStackTrace = getExceptionStackTrace(exception);
            int resultCode = code.code;
            String env = EnvironmentConfig.getInstance().getEnvironment();
            String nameEn = SpringContextHolder.getProjectContextInfo().getNameWithIpPort();
            exceptionCollectionService.collect(now, exception.getClass().getSimpleName(), exceptionStackTrace,
                    resultCode, env, nameEn, exceptionLevel, param);
        }, ExecutorServiceProject.PRIORITY_LOW);

    }


    public static String getExceptionStackTrace(Throwable e) {
        int depth = 0;
        int maxDepth = 2;
        String stack;
        if (!(e instanceof NullPointerException || e instanceof IndexOutOfBoundsException)) {
            stack = getSimpleStackTrace(e);
            try {
                Throwable cc = e.getCause();
                while (cc != null) {
                    String tmp = getSimpleStackTrace(cc);
                    stack = stack.concat("\r\n<br>cause by \r\n<br>").concat(tmp);
                    cc = cc.getCause();
                    depth++ ;
                    if(depth > maxDepth) {
                        break;
                    }
                }
            } catch (Exception exception) {
                log.warn(exception.getMessage());
            }
        } else {
            stack = ExceptionUtils.getStackTrace(e);
            try {
                Throwable cc = e.getCause();
                while (cc != null) {
                    stack = stack.concat("\r\n<br>cause by \r\n<br>").concat(ExceptionUtils.getStackTrace(cc));
                    cc = cc.getCause();
                    depth++ ;
                    if(depth > maxDepth) {
                        break;
                    }
                }
            } catch (Exception exception) {
                log.warn(exception.getMessage());
            }
        }
        return stack;
    }


    public static String getSimpleStackTrace(Throwable e) {
        List<StackTraceElement> list = new ArrayList<>();
        StackTraceElement[] stackTrace = e.getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            String className = stackTraceElement.getClassName();
            if (IGNORE_EX_STACK_PACKAGE.stream().noneMatch(className::startsWith)) {
                list.add(stackTraceElement);
            }
        }

        if (list.size() <= 1) {
            StringBuilder sb = new StringBuilder(e.getClass().getName());
            sb.append("   ").append(e.getMessage()).append("\r\n");
            for(StackTraceElement xx: e.getStackTrace()) {
                sb.append(" at ");
                sb.append(xx.toString());
                sb.append("\r\n");
            }
            return sb.toString();
        } else {
            StringBuilder sb = new StringBuilder(e.getClass().getName());
            sb.append("   ").append(e.getMessage()).append("\r\n");
            for(StackTraceElement xx: list) {
                sb.append(" at ");
                sb.append(xx.toString());
                sb.append("\r\n");
            }
            return sb.toString();
        }
    }


}
