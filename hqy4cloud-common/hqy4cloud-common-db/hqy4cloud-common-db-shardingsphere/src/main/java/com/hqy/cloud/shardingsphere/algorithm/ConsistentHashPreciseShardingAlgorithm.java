package com.hqy.cloud.shardingsphere.algorithm;

import com.hqy.cloud.util.concurrent.ConsistentHash;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 精准查询算法:一致性hash分片算法 </br>
 * 交给子类定义分片的数目和虚拟节点个数
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/30
 */
@Slf4j
public abstract class ConsistentHashPreciseShardingAlgorithm<T extends Comparable<?>> implements PreciseShardingAlgorithm<T> {
    public ConsistentHashPreciseShardingAlgorithm() {
        this(0);
    }

    public ConsistentHashPreciseShardingAlgorithm(int replicates) {
        this.replicates = replicates == 0 ? ConsistentHash.DEFAULT_REPLICAS : replicates;
    }

    private final int replicates;
    private volatile ConsistentHash<String> consistentHash;
    private final Object lock = new Object();


    @Override
    public String doSharding(Collection<String> values, PreciseShardingValue<T> preciseShardingValue) {

        if (consistentHash == null) {
            synchronized (lock) {
                if (consistentHash == null) {
                    consistentHash = new ConsistentHash<>(replicates, new ArrayList<>(values));
                }
            }
        }

        T key = preciseShardingValue.getValue();
        String result = consistentHash.get(key);

        if (!values.contains(result)) {
            log.warn("Consistent hash values not contains {}.", result);
            // refresh
            result = consistentHash.refreshAndGet(key, new ArrayList<>(values));
        }
        return result;
    }



}
