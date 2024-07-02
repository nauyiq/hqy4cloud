package com.hqy.cloud.util.authentication;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/11
 */
public interface AuthorizationService {

    /**
     * 生成验证secret
     * @param authorization   认证对象
     * @return                authorization token
     */
    <T> String encryptAuthorization(T authorization);

    /**
     * 解密authorization, 并且返回业务id
     * @param authorization      认证token
     * @param authorizationClass 解密后的认证类型
     * @return                   解密后的对象
     */
    <T> T decryptAuthorization(String authorization, Class<T> authorizationClass);


}
