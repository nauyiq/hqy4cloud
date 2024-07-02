package com.hqy.cloud.cache.common;

import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;

/**
 * 默认的key生成的策略
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/18
 */
public class IKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        // 分类
        String simpleName = target.getClass().getSimpleName();
        StringBuilder sb = new StringBuilder();
        sb.append(simpleName)
                .append(RedisConstants.CACHE_KEY_SEPARATOR);
        for (Object param : params) {
            sb.append(param.toString());
        }
        return sb.toString();
    }
}
