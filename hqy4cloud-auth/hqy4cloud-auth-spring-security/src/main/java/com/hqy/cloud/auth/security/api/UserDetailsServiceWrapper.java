package com.hqy.cloud.auth.security.api;

import org.springframework.core.Ordered;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 加强UserDetailsService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/18
 */
public interface UserDetailsServiceWrapper extends UserDetailsService, Ordered {

    /**
     * 是否支持此客户端校验
     * @param clientId  目标客户端
     * @param grantType 授权类型
     * @return true/false
     */
    default boolean support(String clientId, String grantType) {
        return true;
    }

    /**
     * 排序值 默认取最大的
     * @return 排序值
     */
    @Override
    default int getOrder() {
        return 0;
    }

}
