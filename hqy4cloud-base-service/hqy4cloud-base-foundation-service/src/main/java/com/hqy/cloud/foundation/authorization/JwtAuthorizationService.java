package com.hqy.cloud.foundation.authorization;

import com.hqy.cloud.util.crypto.symmetric.JWT;
import com.hqy.foundation.authorization.AuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/16
 */
@Slf4j
public class JwtAuthorizationService implements AuthorizationService {

    @Value("${hqy4cloud.authorization.jwt.secret}")
    private String secret;
    @Value("${hqy4cloud.authorization.jwt.expired-seconds:10000}")
    private int expiredSeconds;

    @Override
    public <T> String encryptAuthorization(T authorization) {
        return JWT.getInstance(secret).encrypt(authorization, expiredSeconds);
    }

    @Override
    public <T> T decryptAuthorization(String authorization, Class<T> authorizationClass) {
        boolean expired = JWT.getInstance(secret).isExpired(authorization);
        if (expired) {
            return null;
        }
        return JWT.getInstance(secret).decrypt(authorization, authorizationClass);
    }
}


