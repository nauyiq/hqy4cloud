package com.hqy.cloud.collection.core.exception;

import cn.hutool.core.date.DateUtil;
import com.hqy.cloud.coll.struct.PfExceptionStruct;
import com.hqy.cloud.collection.api.Collector;
import com.hqy.cloud.collection.common.BusinessCollectionType;
import com.hqy.cloud.collection.core.CollectorHolder;
import com.hqy.cloud.registry.context.ProjectContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/7 15:40
 */
@Slf4j
public class ExceptionCollectorUtils {


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


    public static void collect(ExceptionCollActionEvent event) {
        // 构造异常堆栈
        String exceptionStackTrace = getExceptionStackTrace(event.getException());
        // 环境
        String env = ProjectContext.getContextInfo().getEnv();
        // 服务名
        String nameEn = ProjectContext.getContextInfo().getNameWithIpPort();
        // 构建异常rpc对象
        PfExceptionStruct struct = buildStruct(env, nameEn, exceptionStackTrace, event);
        // 调用采集器采集异常
        Collector<PfExceptionStruct> collector = CollectorHolder.getInstance().getCollector(BusinessCollectionType.EXCEPTION);
        collector.collect(struct);
    }

    private static PfExceptionStruct buildStruct(String env, String nameEn, String exceptionStackTrace, ExceptionCollActionEvent event) {
        return PfExceptionStruct.builder()
                .ip(event.getIp())
                .url(event.getUrl())
                .environment(env)
                .exceptionClass(event.getException().getClass().getSimpleName())
                .serviceName(nameEn)
                .stackTrace(exceptionStackTrace)
                .type(event.getType().name())
                .resultCode(event.getResultCode().getCode())
                .params(event.getParam())
                .created(DateUtil.formatDateTime(event.getTime())).build();
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
