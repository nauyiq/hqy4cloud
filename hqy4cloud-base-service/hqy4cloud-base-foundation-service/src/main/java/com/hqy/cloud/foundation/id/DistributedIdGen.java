package com.hqy.cloud.foundation.id;

import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.id.service.RemoteLeafService;
import com.hqy.cloud.id.struct.ResultStruct;
import com.hqy.cloud.rpc.starter.client.RpcClient;
import com.hqy.cloud.util.identity.ProjectSnowflakeIdWorker;
import com.hqy.cloud.util.spring.SpringContextHolder;
import com.hqy.foundation.id.SnowflakeIdGen;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;

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

    private volatile static SnowflakeIdGen snowflakeIdGen;

    /**
     * 获取分布式雪花id
     * @param key request key.
     * @return id.
     */
    public static long getSnowflakeId(String key) {
        if (CommonSwitcher.ENABLE_USING_REDIS_SNOWFLAKE_WORKER_ID.isOn()) {
            if (snowflakeIdGen == null) {
                synchronized (DistributedIdGen.class) {
                    if (snowflakeIdGen == null) {
                        snowflakeIdGen = new RedisSnowflakeIdGen(SpringContextHolder.getBean(RedissonClient.class));
                    }
                }
            }
            return snowflakeIdGen.nextId();
        }

        try {
            RemoteLeafService leafService = RpcClient.getRemoteService(RemoteLeafService.class);
            ResultStruct struct = leafService.getSnowflakeNextId(key);
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


}
