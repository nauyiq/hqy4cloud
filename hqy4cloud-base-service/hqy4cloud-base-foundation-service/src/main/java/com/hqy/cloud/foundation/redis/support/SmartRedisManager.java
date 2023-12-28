package com.hqy.cloud.foundation.redis.support;

import com.hqy.cloud.foundation.redis.AbstractStringRedisAdaptor;
import com.hqy.cloud.util.spring.SpringContextHolder;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Objects;

/**
 * SmartRedisManager.
 * 基于StringRedisTemplate + JsonUtil
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/26 15:15
 */
public class SmartRedisManager extends AbstractStringRedisAdaptor {

    protected SmartRedisManager(StringRedisTemplate redisTemplate) {
        super(redisTemplate);
    }

    private static volatile SmartRedisManager instance = null;

    public static SmartRedisManager getInstance() {
        if (Objects.isNull(instance)) {
            synchronized (SmartRedisManager.class) {
                if (Objects.isNull(instance)) {
                    instance = new SmartRedisManager(SpringContextHolder.getBean(StringRedisTemplate.class));
                }
            }
        }
        return instance;
    }

}
