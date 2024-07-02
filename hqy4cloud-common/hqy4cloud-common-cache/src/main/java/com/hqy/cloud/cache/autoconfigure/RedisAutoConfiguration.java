package com.hqy.cloud.cache.autoconfigure;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hqy.cloud.cache.common.RedisConstants;
import com.hqy.cloud.cache.redis.stream.RedisStreamMessageListener;
import com.hqy.cloud.cache.redis.stream.RedisStreamService;
import com.hqy.cloud.cache.redis.stream.support.*;
import com.hqy.cloud.stream.api.StreamConsumer;
import com.hqy.cloud.stream.api.StreamConsumerFactory;
import com.hqy.cloud.stream.api.StreamProducer;
import com.hqy.cloud.stream.api.StreamProducerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * redis配置类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/18
 */
@Configuration
@AutoConfigureBefore(org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class)
public class RedisAutoConfiguration {


    @ConditionalOnBean
    @Bean(name = RedisConstants.DEFAULT_REDIS_TEMPLATE_BEAN_NAME)
    @ConditionalOnMissingBean(name = RedisConstants.DEFAULT_REDIS_TEMPLATE_BEAN_NAME)
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        //配置jackson的序列化器
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
        //String 序列化器
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // Hash key序列化
        template.setHashKeySerializer(jackson2JsonRedisSerializer);
        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        // 配置连接工厂
        template.setConnectionFactory(factory);
        return template;
    }



    @Bean
    @ConditionalOnMissingBean
    public RedisStreamService redisStreamService(RedisTemplate<String, Object> redisTemplate) {
        return new RedisStreamServiceImpl(redisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisStreamMessageProducer redisStreamMessageProducer(RedisStreamService redisStreamService) {
        StreamProducer.Config config = StreamProducer.Config.builder()
                .supportAsyncApi(false)
                .build();
        StreamProducerFactory<String> streamProducerFactory = new RedisStreamStreamProducerFactory(redisStreamService);
        return (RedisStreamMessageProducer) streamProducerFactory.create(config);
    }

    @Bean
    @ConditionalOnProperty(name = RedisConstants.REDIS_STREAM_CONSUMER_ENABLED, havingValue = "true")
    @ConditionalOnMissingBean
    public StreamConsumer.Config consumerConfig() {
        return StreamConsumer.Config.builder()
                .build();
    }

    @Bean
    @Lazy
    @ConditionalOnBean(RedisStreamMessageListener.class)
    @ConditionalOnMissingBean
    public RedisStreamConsumer redisStreamConsumer(RedisStreamService redisStreamService, RedisStreamMessageListener listener, StreamConsumer.Config config) {
        StreamConsumerFactory factory = new RedisStreamConsumerFactory(redisStreamService, listener);
        return (RedisStreamConsumer) factory.create(config);
    }


}
