package com.hqy.rpc.thrift.ex;

import com.facebook.swift.service.ThriftService;
import com.hqy.base.common.base.project.MicroServiceConstants;
import com.hqy.base.common.rpc.api.RPCService;
import com.hqy.coll.service.CollPersistService;
import com.hqy.util.JsonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 远程调用上下文检查员。 用于判断当前rpc调用是否需要进行异常采集、是否是接入seata需要传递分布式事务id的服务等 <br>
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/25 10:05
 */
@SuppressWarnings("unchecked")
public class RemoteContextChecker {

    private static final Logger log = LoggerFactory.getLogger(RemoteContextChecker.class);

    public static final String IGNORE_COLLECT_KEY = "IGNORE_COLLECT";

    private RemoteContextChecker() {
    }

    /**
     * 忽略异常采集的rpc方法集合
     */
    private static final Set<String> IGNORE_METHOD = new CopyOnWriteArraySet<>();

    /**
     * 需要传播seata事务id的rpc方法集合
     */
    private static final Set<String> TRANSACTION_METHOD = new CopyOnWriteArraySet<>();


    static {
        Class<? extends RPCService>[] ignoreClassArray = new Class[]{
                CollPersistService.class
        };

        for (Class<? extends RPCService> rpcClass : ignoreClassArray) {
            addIgnore(rpcClass);
        }
    }

    /**
     * 忽略异常采集 某个rpc方法 .
     * @param methodName rpc方法名
     */
    public static void addIgnore(String methodName) {
        if (StringUtils.isEmpty(methodName)) {
            return;
        }
        IGNORE_METHOD.add(methodName);
    }

    /**
     * 忽略异常采集 某个rpc类下的所有方法
     * @param ignoreClass rpc类
     */
    public static void addIgnore(@Nonnull Class<? extends RPCService> ignoreClass) {
        try {
            ThriftService thriftService = ignoreClass.getAnnotation(ThriftService.class);
            String methodPrefix = null;
            if (thriftService != null) {
                methodPrefix = thriftService.value();
            }
            Method[] methods = ignoreClass.getMethods();
            for (Method m : methods) {
                if (StringUtils.hasText(methodPrefix)) {
                    IGNORE_METHOD.add(methodPrefix + "." + m.getName());
                } else {
                    IGNORE_METHOD.add(m.getName());
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 标记某个rpc方法需要开启分布式事务.
     * @param methodName rpc方法名
     */
    public static void addTransaction(String methodName) {
        if (StringUtils.isEmpty(methodName)) {
            return;
        }
        TRANSACTION_METHOD.add(methodName);
    }

    /**
     * 标记某个rpc类下的所有rpc方法需要开启分布式事务.
     * @param rpcClass rpc类
     */
    public static void addTransaction(@Nonnull Class<? extends RPCService> rpcClass) {
        try {
            ThriftService thriftService = rpcClass.getAnnotation(ThriftService.class);
            String methodPrefix = null;
            if (thriftService != null) {
                methodPrefix = thriftService.value();
            }
            Method[] methods = rpcClass.getMethods();
            for (Method m : methods) {
                if (StringUtils.hasText(methodPrefix)) {
                    TRANSACTION_METHOD.add(methodPrefix + "." + m.getName());
                } else {
                    TRANSACTION_METHOD.add(m.getName());
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 判断是否需要进行RPC采集？ 如果是，需要在ThriftServerStatsEventHandler 中客户端调用端采集异常或者慢的RPC方法
     * @param methodName rpc方法名
     * @return true or false
     */
    public static boolean needCollect(String methodName) {
        //采集服务的rpc方法无需再进行rpc采集
        if (methodName.startsWith(MicroServiceConstants.COMMON_COLLECTOR)) {
            return false;
        }

        try {
            String ignoreSetJson = System.getProperty(IGNORE_COLLECT_KEY);
            if (!StringUtils.isEmpty(ignoreSetJson)) {
                Set<String> ignoreSet = JsonUtil.toBean(ignoreSetJson, Set.class);
                if (CollectionUtils.isNotEmpty(ignoreSet)) {
                    IGNORE_METHOD.addAll(ignoreSet);
                    System.setProperty(IGNORE_COLLECT_KEY, "");
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return !IGNORE_METHOD.contains(methodName);
    }

    /**
     * 判断当前rpc方法是否需要开启分布式事务. 如果是则需要进行xid的传播.
     * @param methodName rpc方法
     * @return true or false
     */
    public static boolean isTransactional(String methodName) {
        if (methodName.startsWith(MicroServiceConstants.COMMON_COLLECTOR)) {
            return false;
        }

        return TRANSACTION_METHOD.contains(methodName);
    }






}
