package com.hqy.foundation.router;

import java.util.Map;
import java.util.Set;

/**
 * HashRouterService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/10
 */
public interface HashRouterService {

    /**
     * 添加或更新hash对应的路由地址.
     * @param application 应用名
     * @param hash        hash值
     * @param hostAddress ip + 端口
     */
    void updateHashRoute(String application, int hash, String hostAddress);

    /**
     * 获取hash值对应的路由地址.
     * @param application 应用名
     * @param hash        hash值
     * @return            路由地址, ip + 端口
     */
    String getAddress(String application, int hash);

    /**
     * 批量获取hash值对应的路由地址
     * @param application 应用名
     * @param hashSet     hash集合
     * @return            key： hash值, value：路由地址
     */
    Map<Integer, String> getAddress(String application, Set<Integer> hashSet);




}
