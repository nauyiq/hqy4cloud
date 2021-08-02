package com.hqy.cache.redis;

import com.hqy.util.spring.SpringContextHolder;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Objects;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-02 9:41
 */
public class LettuceRedisUtil extends AbstractRedisTemplateUtil {

    private LettuceRedisUtil(RedisTemplate<String, Object> redisTemplate) {
        super(redisTemplate);
    }

    private static LettuceRedisUtil instance = null;

    public LettuceRedisUtil getInstance() {
        if (Objects.isNull(instance)) {
            synchronized (LettuceRedisUtil.class) {
                if (Objects.isNull(instance)) {
                    instance = SpringContextHolder.getBean("LettuceRedisTemplate");
                }
            }
        }
        return instance;
    }

}
