package com.hqy.cloud.util.crypto.symmetric;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hqy.cloud.util.crypto.AbstractSymmetricSymmetric;
import com.hqy.cloud.util.crypto.CryptoType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.hqy.cloud.common.base.lang.AuthConstants.JWT_EXP;
import static com.hqy.cloud.common.base.lang.AuthConstants.JWT_PAYLOAD_KEY;


/**
 * JWT.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/15 14:22
 */
public class JWT extends AbstractSymmetricSymmetric {
    private static final Logger log = LoggerFactory.getLogger(AES.class);
    private final static String DEFAULT_SECRET = "XX#$%()(#*!(hongqy#759428167@qq.com";
    private final JWTVerifier verifier;
    private final JWTSigner signer;
    protected static ObjectMapper objectMapper = new ObjectMapper();
    private final static Map<String, JWT> JWT_REPOSITORY = MapUtil.newConcurrentHashMap(2);
    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    public JWT(String secret) {
        super(secret);
        if (StringUtils.isBlank(secret)) {
            throw new UnsupportedOperationException("JWT secret should not be empty.");
        }
        this.verifier = new JWTVerifier(secret);
        this.signer = new JWTSigner(secret);
    }

    public static JWT getInstance() {
        return getInstance(StrUtil.EMPTY);
    }

    public static JWT getInstance(String key) {
        if (StringUtils.isEmpty(key)) {
            key = DEFAULT_SECRET;
        }
        if (JWT_REPOSITORY.containsKey(key)) {
            return JWT_REPOSITORY.get(key);
        }
        String finalKey = key;
        return JWT_REPOSITORY.computeIfAbsent(key, value -> new JWT(finalKey));
    }

    @Override
    public CryptoType getType() {
        return CryptoType.JWT;
    }

    @Override
    public <T> String encrypt(T data, long expiredSeconds) {
        HashMap<String, Object> claims = MapUtil.newHashMap(4);
        try {
            String dataStr;
            if (data instanceof String) {
                dataStr = (String) data;
            } else {
                dataStr = objectMapper.writeValueAsString(data);
            }
            claims.put(JWT_PAYLOAD_KEY, dataStr);
            claims.put(JWT_EXP, System.currentTimeMillis() + (expiredSeconds * 1000));
            return signer.sign(claims);
        } catch (Throwable cause) {
            log.warn("Failed execute to jwt sign, cause: {}", cause.getMessage());
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T decrypt(String encryptContent, Class<T> clazz) {
        try {
            final Map<String, Object> claims = verifier.verify(encryptContent);
            if (claims.containsKey(JWT_EXP) && claims.containsKey(JWT_PAYLOAD_KEY)) {
                long exp = (long) claims.get(JWT_EXP);
                if (exp > System.currentTimeMillis()) {
                    String json = (String) claims.get(JWT_PAYLOAD_KEY);
                    if (Objects.nonNull(clazz)) {
                        return objectMapper.readValue(json, clazz);
                    }
                    return (T) json;
                }
            }
        } catch (Throwable cause) {
            log.warn("Failed execute to un");
        }
        return null;
    }

    @Override
    public boolean isExpired(String encryptContent) {
        try {
            Map<String, Object> claims = verifier.verify(encryptContent);
            if (MapUtil.isEmpty(claims)) {
                return true;
            }
            if (claims.containsKey(JWT_EXP) && claims.containsKey(JWT_PAYLOAD_KEY)) {
                long exp = (Long) claims.get(JWT_EXP);
                return System.currentTimeMillis() > exp;
            }
        } catch (Throwable cause) {
            log.error(cause.getMessage(), cause);
        }

        return true;
    }
}
