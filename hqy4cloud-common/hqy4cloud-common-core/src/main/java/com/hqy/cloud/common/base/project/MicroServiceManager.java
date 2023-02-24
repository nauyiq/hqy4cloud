package com.hqy.cloud.common.base.project;

import com.hqy.cloud.common.base.lang.ActuatorNodeEnum;

import java.util.*;

/**
 * 所有微服务模块管理器
 * @author qiyuan.hong
 * @date 2022-02-17 23:56
 */
public class MicroServiceManager {

    /**
     * key :消费者节点英文模块名称， value: 节点模式 <br>
     * 服务提供者需要注册此
     */
    private static final Map<String, ActuatorNodeEnum> PROJECT_NAME_MAP = new HashMap<>();

    /**
     * key:socket.io项目的contextPath
     * value: 项目的注册名
     */
    private static final Map<String, String> SOCKET_CONTEXT_PATH_MAP = new HashMap<>();

    static {
        PROJECT_NAME_MAP.put(MicroServiceConstants.GATEWAY, ActuatorNodeEnum.CONSUMER);
        PROJECT_NAME_MAP.put(MicroServiceConstants.COMMON_COLLECTOR, ActuatorNodeEnum.PROVIDER);
        PROJECT_NAME_MAP.put(MicroServiceConstants.ACCOUNT_SERVICE, ActuatorNodeEnum.PROVIDER);
        PROJECT_NAME_MAP.put(MicroServiceConstants.MESSAGE_NETTY_SERVICE, ActuatorNodeEnum.PROVIDER);
        PROJECT_NAME_MAP.put(MicroServiceConstants.BLOG_SERVICE, ActuatorNodeEnum.CONSUMER);
        PROJECT_NAME_MAP.put(MicroServiceConstants.ADMIN_SERVICE, ActuatorNodeEnum.CONSUMER);
        PROJECT_NAME_MAP.put(MicroServiceConstants.COMMUNICATION_SERVICE, ActuatorNodeEnum.PROVIDER);

        //transaction-test
        PROJECT_NAME_MAP.put(MicroServiceConstants.DEMO_WALLET_SERVICE, ActuatorNodeEnum.PROVIDER);
        PROJECT_NAME_MAP.put(MicroServiceConstants.DEMO_ORDER_SERVICE, ActuatorNodeEnum.PROVIDER);
        PROJECT_NAME_MAP.put(MicroServiceConstants.DEMO_STORAGE_SERVICE, ActuatorNodeEnum.PROVIDER);

        SOCKET_CONTEXT_PATH_MAP.put(MicroServiceConstants.SocketContextPath.MESSAGE_SERVICE, MicroServiceConstants.MESSAGE_NETTY_SERVICE);
    }


    public static boolean checkClusterExist(String clusterName) {
        ActuatorNodeEnum actuatorNodeEnum = PROJECT_NAME_MAP.get(clusterName);
        return Objects.nonNull(actuatorNodeEnum);
    }


    /**
     * 根据服务节点英文模块名称获取服务节点的中文模块名称
     * @param nameEn 服务名
     * @return 节点类型
     */
    public static ActuatorNodeEnum getNodeType(String nameEn) {
        return PROJECT_NAME_MAP.get(nameEn);
    }

    /**
     * 根据socket.io的contextPath获取对应的项目名
     * @param contextPath socket.io contextPath
     * @return 项目名
     */
    public static String getSocketModule(String contextPath) {
        return SOCKET_CONTEXT_PATH_MAP.get(contextPath);
    }

    /**
     * 根据节点类型 获取微服务模块集合
     * @return 微服务模块集合
     */
    public static Set<String> getServiceEnNames(ActuatorNodeEnum actuatorNode) {
        if (Objects.isNull(actuatorNode)) {
            return PROJECT_NAME_MAP.keySet();
        }
        Set<String> actuatorNodeSet = new HashSet<>();
        for (Map.Entry<String, ActuatorNodeEnum> entry : PROJECT_NAME_MAP.entrySet()) {
            ActuatorNodeEnum nodeEnum = entry.getValue();
            if (nodeEnum == actuatorNode) {
                actuatorNodeSet.add(entry.getKey());
            }
        }
        return actuatorNodeSet;
    }

}
