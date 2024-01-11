package com.hqy.foundation.authorization;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/11
 */
public interface AuthorizationService {

    /**
     * 生成验证secret
     * @param applicationName 服务名
     * @param bizId           业务id
     * @return                authorization token
     */
    String getAuthorization(String applicationName, String bizId);

    /**
     * 解密authorization, 并且返回业务id
     * @param authorization 认证token
     * @return              bizId
     */
    String decryptAuthorization(String authorization);


}
