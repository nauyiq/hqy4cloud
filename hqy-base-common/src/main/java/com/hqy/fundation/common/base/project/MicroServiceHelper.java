package com.hqy.fundation.common.base.project;

import com.hqy.fundation.common.base.lang.ActuatorNodeEnum;

import java.util.*;

/**
 * @author qiyuan.hong
 * @date 2022-02-17 23:56
 */
public class MicroServiceHelper {

    /**
     * key :消费者节点英文模块名称， value: 节点模式 <br>
     * 服务提供者需要注册此
     */
    private static final Map<String, ActuatorNodeEnum> PROJECT_NAME_MAP = new HashMap<>();




    static {
        //服务名
        PROJECT_NAME_MAP.put(MicroServiceConstants.GATEWAY, ActuatorNodeEnum.PROVIDER);

        PROJECT_NAME_MAP.put(MicroServiceConstants.COMMON_COLLECTOR, ActuatorNodeEnum.PROVIDER);

    }

    public static boolean checkClusterExist(String clusterName) {
        ActuatorNodeEnum actuatorNodeEnum = PROJECT_NAME_MAP.get(clusterName);
        return Objects.nonNull(actuatorNodeEnum);
    }


    /**
     * 根据服务节点英文模块名称获取服务节点的中文模块名称
     * @param nameEn
     * @return
     */
    public static ActuatorNodeEnum getNodeType(String nameEn) {
        return PROJECT_NAME_MAP.get(nameEn);
    }

    /**
     * 获取英文列表名
     * @return
     */
    public static Set<String> getServiceEnNames() {
        return PROJECT_NAME_MAP.keySet();
    }

}
