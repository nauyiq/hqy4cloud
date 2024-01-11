package com.hqy.foundation.socket;

import java.util.Map;
import java.util.Set;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/10
 */
public interface IdRouterService {

    /**
     * 注册hash对应的路由地址.
     * @param application 应用名
     * @param id          id
     * @param hostAddress ip + 端口
     */
    void register(String application, String id, String hostAddress);

    /**
     * 获取hash值对应的路由地址.
     * @param application 应用名
     * @param id          hash值
     * @return            路由地址, ip + 端口
     */
    String getAddress(String application, String id);

    /**
     * 批量获取hash值对应的路由地址
     * @param application 应用名
     * @param ids         id值
     * @return            key： id, value：路由地址
     */
    Map<Integer, String> getAddress(String application, Set<String> ids);




}
