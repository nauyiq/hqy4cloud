package com.hqy.cloud.util.crypto.symmetric;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.hqy.cloud.util.JsonUtil;
import com.hqy.cloud.util.crypto.AbstractSymmetricSymmetric;
import com.hqy.cloud.util.crypto.CryptoType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.Map;
import java.util.Objects;

/**
 * AES.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/15 13:37
 */
public class AES extends AbstractSymmetricSymmetric {
    private static final Logger log = LoggerFactory.getLogger(AES.class);
    private final cn.hutool.crypto.symmetric.AES aes;
    private final static AES DEFAULT_AES = new AES("MFpXa3VIYjIzbjAxc1VYQw==");
    private final static Map<String, AES> AES_REPOSITORY = MapUtil.newConcurrentHashMap(2);
    private AES(String key) {
        super(key);
        if (StringUtils.isBlank(key)) {
            throw new UnsupportedOperationException("Aes key should not be blank.");
        }
        this.aes = SecureUtil.aes(Base64.getDecoder().decode(key));
    }

    public static AES getInstance() {
        return getInstance(StrUtil.EMPTY);
    }

    public static AES getInstance(String key) {
        if (StringUtils.isEmpty(key)) {
            return DEFAULT_AES;
        }
        if (AES_REPOSITORY.containsKey(key)) {
            return AES_REPOSITORY.get(key);
        }
        return AES_REPOSITORY.computeIfAbsent(key, value -> new AES(key));
    }

    @Override
    public CryptoType getType() {
        return CryptoType.AES;
    }

    @Override
    public <T> String encrypt(T data, long expiredSeconds) {
        if (Objects.isNull(data)) {
            return null;
        }
        String content;
        if (data instanceof String) {
            content = (String) data;
        } else {
            content = JsonUtil.toJson(data);
        }
        //加密为16进制表示
        return aes.encryptHex(content);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T decrypt(String encryptContent, Class<T> clazz) {
        if (StringUtils.isEmpty(encryptContent)) {
            return null;
        }
        try {
            // 解密为字符串
            String decryptStr = aes.decryptStr(encryptContent, CharsetUtil.CHARSET_UTF_8);
            if (Objects.nonNull(clazz)) {
                return JsonUtil.toBean(decryptStr, clazz);
            } else {
                return (T) decryptStr;
            }
        } catch (Throwable cause) {
            log.debug("Failed execute to aes decrypt, encryptContent:{}.", encryptContent);
            return null;
        }
    }

    @Override
    public boolean isExpired(String encryptContent) {
        throw new UnsupportedOperationException();
    }


    public static void main(String[] args) {
        System.out.println(AES.getInstance().encrypt("13751938073"));
        System.out.println(AES.getInstance().encrypt("759428167@qq.com"));
    }

}
