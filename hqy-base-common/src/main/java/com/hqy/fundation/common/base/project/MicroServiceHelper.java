package com.hqy.fundation.common.base.project;

import com.hqy.fundation.common.base.lang.BaseStringConstants;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @date 2022-02-17 23:56
 */
public class MicroServiceHelper {

    /**
     * key :消费者节点英文模块名称， value: 服务节点的中文模块名称 <br>
     * 服务提供者需要注册此
     */
    private static final Map<String, String> PROJECT_NAME_MAP = new HashMap<>();

    static {
        PROJECT_NAME_MAP.put("hqy_gateway", "网关服务");
    }


    /**
     * 根据服务节点英文模块名称获取服务节点的中文模块名称
     * @param nameEn
     * @return
     */
    public static String getName(String nameEn) {
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
