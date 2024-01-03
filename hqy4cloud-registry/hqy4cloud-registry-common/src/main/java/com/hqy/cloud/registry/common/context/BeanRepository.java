package com.hqy.cloud.registry.common.context;

import com.hqy.cloud.util.AssertUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * BeanRepository.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/3
 */
public class BeanRepository {

    private final static Map<Class<?>, Object> BEANS_MAP = new ConcurrentHashMap<>();
    private BeanRepository() {}
    private static final BeanRepository INSTANCE = new BeanRepository();

    public static BeanRepository getInstance() {
        return INSTANCE;
    }

    public void register(Object bean) {
        register(bean.getClass(), bean);
    }

    public void register(Class<?> clazz, Object bean) {
        AssertUtil.notNull(bean, "Register bean should not be null.");
        BEANS_MAP.put(clazz, bean);
    }

    @SuppressWarnings({"unchecked"})
    public <T> T getBean(Class<T> tClass) {
        if(BEANS_MAP.containsKey(tClass)) {
            return (T) BEANS_MAP.get(tClass);
        }
        return null;
    }


}
