package com.hqy.fundation.cache.redis;

import com.hqy.util.spring.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Objects;

/**
 * @author qy
 * @date 2021-08-02 9:41
 */
@Slf4j
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

    @Override
    @SuppressWarnings("rawtypes")
    public AbstractRedisTemplateUtil selectDb(int db, boolean stringTmp) {
        if (db < 0 || db > 15) {
            db = 0;
        }
        LettuceConnectionFactory lettuceConnectionFactory;
        RedisTemplate redisTemplate;
        if (stringTmp) {
            redisTemplate = getStringRedisTemplate();
        } else {
            redisTemplate = getRedisTemplate();
        }
        lettuceConnectionFactory = (LettuceConnectionFactory) redisTemplate.getConnectionFactory();

        if (Objects.isNull(lettuceConnectionFactory)) {
            throw new IllegalStateException("Initialize redisTemplate failure. get redisTemplateConnectionFactory failure.");
        }

        try {
            lettuceConnectionFactory.setDatabase(db);
            super.getRedisTemplate().setConnectionFactory(lettuceConnectionFactory);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return this;
    }
}
