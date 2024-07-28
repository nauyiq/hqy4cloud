package com.hqy.cloud.sharding.algorithm;

import com.hqy.cloud.util.concurrent.ConsistentHash;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

/**
 * 精准查询算法:一致性hash分片算法 </br>
 * 交给子类定义分片的数目和虚拟节点个数
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/30
 */
@Slf4j
public  class ConsistentHashPreciseShardingAlgorithm<T extends Comparable<?>> implements StandardShardingAlgorithm<T> {
    public ConsistentHashPreciseShardingAlgorithm() {
        this(0);
    }

    public ConsistentHashPreciseShardingAlgorithm(int replicates) {
        this.replicates = replicates == 0 ? ConsistentHash.DEFAULT_REPLICAS : replicates;
    }

    private Properties properties;
    private final int replicates;
    private volatile ConsistentHash<String> consistentHash;
    private final Object lock = new Object();

    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, RangeShardingValue<T> shardingValue) {
        return availableTargetNames;
    }


    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<T> shardingValue) {
        if (consistentHash == null) {
            synchronized (lock) {
                if (consistentHash == null) {
                    consistentHash = new ConsistentHash<>(replicates, new ArrayList<>(availableTargetNames));
                }
            }
        }

        T key = shardingValue.getValue();
        String result = consistentHash.get(key);

        if (!availableTargetNames.contains(result)) {
            log.warn("Consistent hash values not contains {}.", result);
            // refresh
            result = consistentHash.refreshAndGet(key, new ArrayList<>(availableTargetNames));
        }
        return result;
    }

    @Override
    public Properties getProps() {
        return properties;
    }

    @Override
    public void init(Properties properties) {
        this.properties = properties;
    }
}
