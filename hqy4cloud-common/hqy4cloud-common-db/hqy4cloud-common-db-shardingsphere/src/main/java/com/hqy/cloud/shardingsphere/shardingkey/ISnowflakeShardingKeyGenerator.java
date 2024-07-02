package com.hqy.cloud.shardingsphere.shardingkey;

import com.hqy.cloud.id.gen.DistributedIdGen;
import com.hqy.cloud.registry.context.ProjectContext;
import org.apache.shardingsphere.spi.keygen.ShardingKeyGenerator;

import java.util.Properties;

/**
 * 通过rpc调用id生成服务生成的雪花id, 与shardingsphere自带的雪花算法却别在于 </br>
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
public class ISnowflakeShardingKeyGenerator implements ShardingKeyGenerator {
    private Properties properties;


    @Override
    public Comparable<?> generateKey() {
        String nameEn = ProjectContext.getContextInfo().getNameEn();
        return DistributedIdGen.getSnowflakeId(nameEn);
    }

    @Override
    public String getType() {
        return "ISNOWFLAKE";
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
