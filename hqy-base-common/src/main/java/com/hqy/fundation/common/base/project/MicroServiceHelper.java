package com.hqy.fundation.common.base.project;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qiyuan.hong
 * @date 2022-02-17 23:56
 */
public class MicroServiceHelper {

    /**
     * key :消费者节点英文模块名称， value: 服务节点的中文模块名称 <br>
     * 服务提供者需要注册此
     */
    private static final Map<String, String> projectNameMap = new HashMap<>();

    /**
     * 根据服务节点英文模块名称获取服务节点的中文模块名称
     * @param nameEn
     * @return
     */
    public static String getName(String nameEn) {
        return projectNameMap.get(nameEn);
    }

}
