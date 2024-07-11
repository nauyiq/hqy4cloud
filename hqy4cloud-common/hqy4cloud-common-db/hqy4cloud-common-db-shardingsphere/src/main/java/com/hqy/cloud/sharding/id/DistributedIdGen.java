package com.hqy.cloud.sharding.id;

import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.id.service.RemoteLeafService;
import com.hqy.cloud.id.struct.ResultStruct;
import com.hqy.cloud.rpc.starter.client.RpcClient;
import com.hqy.cloud.sharding.strategy.ShardingTableStrategy;
import com.hqy.cloud.sharding.strategy.support.DefaultShardingTableStrategy;
import com.hqy.cloud.util.AssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分布式id成器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/22 15:18
 */
@Slf4j
public class DistributedIdGen {

    public static long getSegmentId(String key) {
        if (StringUtils.isBlank(key)) {
            throw new UnsupportedOperationException("BizTag key should not be empty.");
        }
        RemoteLeafService leafService = RpcClient.getRemoteService(RemoteLeafService.class);
        ResultStruct struct = leafService.getSegmentId(key);
        if (!struct.isResult()) {
            throw new IllegalArgumentException("Failed execute to get segment id, result code = " + struct.id);
        }
        return struct.id;
    }


    private static final Map<String, SnowflakeIdGen> ID_MAP = new ConcurrentHashMap<>();

    /**
     * 获取分布式雪花id
     * @param scene 使用场景
     * @return id.
     */
    public static long getSnowflakeId(String scene) {
        AssertUtil.notEmpty(scene, "Snowflake scene should not be empty.");
        if (CommonSwitcher.ENABLE_USING_REDIS_SNOWFLAKE_WORKER_ID.isOn()) {
            SnowflakeIdGen idGen = ID_MAP.computeIfAbsent(scene, v -> new RedisSnowflakeIdGen(scene, SpringUtil.getBean(RedissonClient.class)));
            return idGen.nextId();
        }

        try {
            RemoteLeafService leafService = RpcClient.getRemoteService(RemoteLeafService.class);
            ResultStruct struct = leafService.getSnowflakeNextId(scene);
            if (!struct.result) {
                log.warn("Failed execute to get id by rpc, error code: {}.", struct.id);
            } else {
                return struct.id;
            }
        } catch (Throwable exception) {
            log.warn("Failed execute to get id by rpc, cause: {}.", exception.getMessage(), exception);
        }

        throw new UnsupportedOperationException("No supported distributed id.");
    }

    /**
     * 默认的分表策略.
     */
    public static ShardingTableStrategy strategy = new DefaultShardingTableStrategy();


    public static long getSnowflakeId(long workerId) {
        return IdUtil.getSnowflake(workerId).nextId();
    }

    public static String generateWithSnowflake(String businessCode, int tableCount, long workerId, long externalId) {
        long seq = IdUtil.getSnowflake(workerId).nextId();
        return generate(businessCode, tableCount, externalId, seq);
    }

    /**
     * 生成分布式id 由业务code + 外部id（机器序列号） + 表下标
     * @param businessCode   业务code， 应该为数值类型
     * @param tableCount     分表的数目
     * @param externalId     外部id
     * @param sequenceNumber 机器序列号
     * @return               分布式id
     */
    public static String generate(String businessCode, int tableCount, long externalId, long sequenceNumber) {
        tableCount = tableCount > 0 ? tableCount : 1;
        // 表下标.
        String tableIndex = String.valueOf(strategy.getTableIndex(String.valueOf(externalId), tableCount));
        DistributedID id = DistributedID.create(String.valueOf(businessCode), sequenceNumber, tableIndex);
        return id.toString();
    }







}
