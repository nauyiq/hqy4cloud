package com.hqy.cloud.id.component.snowflake.core.support;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.foundation.redis.key.RedisKey;
import com.hqy.cloud.foundation.redis.key.support.RedisNamedKey;
import com.hqy.cloud.id.component.snowflake.core.AbstractSnowflakeHolder;
import com.hqy.cloud.id.component.snowflake.exception.InitWorkerIdException;
import com.hqy.cloud.registry.common.context.Environment;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.util.Collection;
import java.util.Collections;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/23 14:27
 */
@RequiredArgsConstructor
public class SnowflakeRedisHolder extends AbstractSnowflakeHolder {

    private final Environment environment;
    private final RedisKey redisKey = new RedisNamedKey(MicroServiceConstants.ID_SERVICE, "Snowflake");
    private final RedissonClient redissonClient;

    @Override
    protected void doInit(String serviceName) {
        String environment = this.environment.getEnvironment();
        String ipAddr = NetUtil.getLocalhostStr();
        RMap<String, Integer> map = redissonClient.getMap(redisKey.getKey());
        String key = ipAddr.concat(StrUtil.DOT).concat(environment);
        if (!map.containsKey(key)) {
            int workerId;
            Collection<Integer> values = map.values();
            if (CollectionUtils.isEmpty(values)) {
                workerId = 0;
            } else {
                Integer max = Collections.max(values);
                if (max >= MAX_WORKER_ID) {
                    throw new InitWorkerIdException("WorkerId must gte 0 and lte 1023");
                }
                workerId = ++max;
            }
            map.put(key, workerId);
            setWorkerId(workerId);
        } else {
            setWorkerId(map.get(key));
        }
    }
}
