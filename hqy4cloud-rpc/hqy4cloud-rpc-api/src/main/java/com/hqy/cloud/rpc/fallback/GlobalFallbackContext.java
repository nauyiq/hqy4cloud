package com.hqy.cloud.rpc.fallback;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.util.AssertUtil;

import java.util.Map;
import java.util.ServiceLoader;

/**
 * GlobalFallbackContext.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/5 17:21
 */
public class GlobalFallbackContext {

    private static final Map<Class<? extends Exception>, Fallback> FALLBACK_REPOSITORY = MapUtil.newConcurrentHashMap(4);

    static {
        ServiceLoader<Fallback> serviceLoader = ServiceLoader.load(Fallback.class);
        for (Fallback fallback : serviceLoader) {
            FALLBACK_REPOSITORY.put(fallback.exceptionType(), fallback);
        }
    }

    public static void setFallback(Fallback fallback) {
        AssertUtil.notNull(fallback, "Fallback should not be null.");
        FALLBACK_REPOSITORY.put(fallback.exceptionType(), fallback);
    }

    public static Fallback getFallback(Class<? extends Throwable> exType) {
        return FALLBACK_REPOSITORY.get(exType);
    }




}
