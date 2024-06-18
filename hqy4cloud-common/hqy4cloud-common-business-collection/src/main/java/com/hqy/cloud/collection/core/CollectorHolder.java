package com.hqy.cloud.collection.core;

import cn.hutool.core.map.MapUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.hqy.cloud.collection.api.Collector;
import com.hqy.cloud.collection.common.BusinessCollectionType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 采集器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/12
 */
@Component
public class CollectorHolder implements CommandLineRunner {

    public static CollectorHolder getInstance() {
        return SpringUtil.getBean(CollectorHolder.class);
    }


    private static final Map<BusinessCollectionType, Collector<?>> COLLECTOR_MAP = MapUtil.newConcurrentHashMap();

    /*static {
        CollectionConfig sqlConfig = new CollectionConfig(true, 1);
        CollectionConfig exceptionConfig = new CollectionConfig(true, 50);
        CollectionConfig throttleConfig = new CollectionConfig(true, 1);
    }*/


    @SuppressWarnings("unchecked")
    public <T> Collector<T> getCollector(BusinessCollectionType type) {
        return (Collector<T>) COLLECTOR_MAP.get(type);
    }

    @Override
    public void run(String... args) throws Exception {
        // 从容器加载所有的采集器到当前类中
        SpringUtil.getBeansOfType(Collector.class).values().forEach(collector -> {COLLECTOR_MAP.put(collector.type(), collector);});
    }

}
