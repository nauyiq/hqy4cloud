package com.hqy.cloud.rpc.core;

import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.util.JsonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 远程调用上下文检查员。 用于判断当前rpc调用是否需要进行异常采集、是否是接入seata需要传递分布式事务id的服务等 <br>
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/25 10:05
 */
@Component
@SuppressWarnings("unchecked")
public class RemoteContextChecker {

    private static final Logger log = LoggerFactory.getLogger(RemoteContextChecker.class);

    public static final String IGNORE_COLLECT_KEY = "IGNORE_COLLECT";

    /**
     * 忽略异常采集的rpc方法集合
     */
    private static final Set<String> IGNORE_METHOD = new CopyOnWriteArraySet<>();

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
     * 判断是否需要进行RPC采集？ 如果是，需要在ThriftServerStatsEventHandler 中客户端调用端采集异常或者慢的RPC方法
     * @param methodName rpc方法名
     * @return true or false
     */
    public static boolean needCollect(String methodName) {
        //采集服务的rpc方法无需再进行rpc采集
        if (methodName.startsWith(MicroServiceConstants.COMMON_COLLECTOR) || CommonSwitcher.ENABLE_THRIFT_RPC_COLLECT.isOff()) {
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








}
