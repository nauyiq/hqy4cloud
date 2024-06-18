package com.hqy.cloud.util.spi;

import cn.hutool.core.map.MapUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.util.*;

/**
 * 通用的 spi loader
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/19 15:39
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpiInstanceServiceLoad {
    private static final Map<Class<?>, Collection<Class<?>>> SERVICE_MAP = MapUtil.newConcurrentHashMap();

    /**
     * 注册加载spi服务
     * @param service spi service
     */
    public static <T> void register(final Class<T> service) {
        for (T each : ServiceLoader.load(service)) {
            registerServiceClass(service, each);
        }
    }

    private static <T> void registerServiceClass(final Class<T> service, final T instance) {
        Collection<Class<?>> serviceClasses = SERVICE_MAP.get(service);
        if (null == serviceClasses) {
            serviceClasses = new LinkedHashSet<>();
        }
        serviceClasses.add(instance.getClass());
        SERVICE_MAP.put(service, serviceClasses);
    }

    /**
     * 获取spi接口的实例列表
     * 要求spi service 持有无参的构造
     * @param service spi接口
     * @return        spi service instances
     */
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static <T> Collection<T> getServiceInstances(final Class<T> service) {
        Collection<T> result = new LinkedList<>();
        if (null == SERVICE_MAP.get(service)) {
            return result;
        }
        for (Class<?> each : SERVICE_MAP.get(service)) {
            result.add((T) each.getDeclaredConstructor().newInstance());
        }
        return result;
    }




}
