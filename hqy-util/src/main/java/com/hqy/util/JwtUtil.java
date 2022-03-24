package com.hqy.util;

import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.internal.com.fasterxml.jackson.databind.DeserializationFeature;
import com.auth0.jwt.internal.com.fasterxml.jackson.databind.ObjectMapper;
import com.hqy.base.common.base.lang.BaseStringConstants;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qiyuan.hong
 * @date 2022-03-14 13:27
 */
@Slf4j
public class JwtUtil {

    private JwtUtil() {
    }

    protected static final String SECRET = "XX#$%()(#*!()!KL<>759428167@qq.com";

    protected static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 将对象签名为JWT的token字符串，（指定有效时间）
     * @param object 需要加密的对象
     * @param maxAge 单位 毫秒
     * @return the jwt token
     */
    public static <T> String sign(T object, long maxAge) {
        try {
            final JWTSigner signer = new JWTSigner(SECRET);
            final Map<String, Object> claims = new HashMap<>(6);
            String jsonString = objectMapper.writeValueAsString(object);
            claims.put(BaseStringConstants.Auth.JWT_PAYLOAD_KEY, jsonString);
            claims.put(BaseStringConstants.Auth.JWT_EXP, System.currentTimeMillis() + maxAge);
            return signer.sign(claims);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 校验jwt token 是否有效
     * @param jwtToken JWT的token字符串
     * @return the jwt validation
     */
    public static boolean validate(String jwtToken) {
        final JWTVerifier verifier = new JWTVerifier(SECRET);
        Map<String, Object> claims;
        try {
            claims = verifier.verify(jwtToken);
            if (claims == null || claims.isEmpty()) {
                return false;
            }
            if (claims.containsKey(BaseStringConstants.Auth.JWT_EXP) &&
                    claims.containsKey(BaseStringConstants.Auth.JWT_PAYLOAD_KEY)) {
                long exp = (Long) claims.get(BaseStringConstants.Auth.JWT_EXP);
                long currentTimeMillis = System.currentTimeMillis();
                return exp > currentTimeMillis;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * 解析jwt token 生成token签名加密前的对象
     * @param jwt jwt token
     * @param clazz 加密前的对象class
     * @return 签名加密前的对象
     */
    public static <T> T unSign(String jwt, Class<T> clazz) {
        final JWTVerifier verifier = new JWTVerifier(SECRET);
        try {
            final Map<String, Object> claims = verifier.verify(jwt);
            if (claims.containsKey(BaseStringConstants.Auth.JWT_EXP) && claims.containsKey(BaseStringConstants.Auth.JWT_PAYLOAD_KEY)) {
                long exp = (long) claims.get(BaseStringConstants.Auth.JWT_EXP);
                if (exp > System.currentTimeMillis()) {
                    String json = (String) claims.get(BaseStringConstants.Auth.JWT_PAYLOAD_KEY);
                    return objectMapper.readValue(json, clazz);
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }




}
