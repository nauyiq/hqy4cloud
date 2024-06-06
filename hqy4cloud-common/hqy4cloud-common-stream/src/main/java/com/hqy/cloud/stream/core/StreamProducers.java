package com.hqy.cloud.stream.core;

import com.google.common.collect.Maps;
import com.hqy.cloud.stream.api.StreamProducer;
import com.hqy.cloud.util.spring.SpringContextHolder;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.InitializingBean;

import java.util.Map;

/**
 * 发布订阅模型，生产者入口.
 * 需要注意的是，需要由实际的发布订阅模块实现发布订阅的模型，通过Spring加载所有的Producer子类
 * 因此业务使用该模型时，赢确保引入了实际要用的发布订阅的中间件模块，比如redis(foundation模块)、kafka(mq-kafka模块)
 * 当业务引入了多个中间件的发布订阅模型时，应该指定默认的发布订阅模型或者使用api时指定发布订阅模型的类型
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/4/28
 */
public class StreamProducers implements InitializingBean {
    private static final Map<String, StreamProducer<?>> MAP = Maps.newHashMapWithExpectedSize(4);


    @Override
    @SuppressWarnings("rawtypes")
    public void afterPropertiesSet() throws Exception {
        // 获取注册到容器的producer类, 注入到当前上下文中
        Map<String, StreamProducer> producerMap = SpringContextHolder.getBeansOfType(StreamProducer.class);
        if (MapUtils.isNotEmpty(producerMap)) {
            producerMap.values().forEach(producer -> MAP.put(producer.getType(), producer));
        }
    }
}
