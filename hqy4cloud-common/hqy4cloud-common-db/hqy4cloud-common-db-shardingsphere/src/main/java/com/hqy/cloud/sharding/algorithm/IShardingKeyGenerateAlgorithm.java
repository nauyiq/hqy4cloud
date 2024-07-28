package com.hqy.cloud.sharding.algorithm;

import com.hqy.cloud.sharding.id.DistributedIdGen;
import com.hqy.cloud.sharding.id.WorkerIdHolder;
import org.apache.shardingsphere.sharding.spi.KeyGenerateAlgorithm;

import java.util.Properties;

/**
 * shardingsphere提供的雪花算法需要自己设置workerId, 即
 * Properties snowflakeProp = new Properties();
 * snowflakeProp.setProperty("worker-id", workerId);
 * shardingRuleConfiguration.getKeyGenerators().put("snowflake", new ShardingSphereAlgorithmConfiguration("SNOWFLAKE", snowflakeProp));
 * 或者配置文件指定workerId
 * spring.shardingsphere.sharding.tables.**.key-generator.props.worker.id=1
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/30
 */
public class IShardingKeyGenerateAlgorithm implements KeyGenerateAlgorithm {
    private Properties properties;


    @Override
    public Comparable<?> generateKey() {
        return DistributedIdGen.getSnowflakeId(WorkerIdHolder.workerId);
    }

    @Override
    public String getType() {
        return "ISNOWFLAKE";
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
