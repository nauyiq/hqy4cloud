package com.hqy.fundation.cache.redis;

import com.hqy.util.spring.SpringContextHolder;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Objects;

/**
 * @author qy
 * @date  2021-08-02 9:41
 */
public class LettuceRedis extends AbstractRedisTemplateUtil {

    private LettuceRedis(RedisTemplate<String, Object> redisTemplate) {
        super(redisTemplate);
    }

    private static volatile LettuceRedis instance = null;

    public static LettuceRedis getInstance() {
        if (Objects.isNull(instance)) {
            synchronized (LettuceRedis.class) {
                if (Objects.isNull(instance)) {
                    @SuppressWarnings("unchecked")
                    RedisTemplate<String, Object> template = SpringContextHolder.getBean(RedisTemplate.class, "LettuceRedisTemplate");
                    instance = new LettuceRedis(template);
                }
            }
        }
        return instance;
    }

}
