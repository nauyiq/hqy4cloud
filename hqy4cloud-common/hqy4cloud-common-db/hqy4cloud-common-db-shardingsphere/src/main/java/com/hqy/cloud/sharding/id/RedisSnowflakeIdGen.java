package com.hqy.cloud.sharding.id;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.common.base.lang.DateMeasureConstants;
import com.hqy.cloud.id.dto.SnowFlakeDTO;
import com.hqy.cloud.registry.context.ProjectContext;
import com.hqy.cloud.util.concurrent.IExecutorsRepository;
import com.hqy.cloud.common.base.project.ProjectContextInfo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 基于redis 获取workerId的雪花算法 service
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/30
 */
@Slf4j
public class RedisSnowflakeIdGen extends AbstractSnowflakeIdGen {
    private static final long EXPIRED = DateMeasureConstants.ONE_MONTH.toMillis();
    private static final long MAX_WORKER_ID = 1023;
    private final String KEY;
    private final RedissonClient redissonClient;
    private final long workerId;

    public RedisSnowflakeIdGen(String scene, RedissonClient redissonClient) {
        ProjectContextInfo contextInfo = ProjectContext.getContextInfo();
        String name = "workerId:" + scene;
        KEY = contextInfo.getNameEn() + StrUtil.COLON + contextInfo.getEnv() + StrUtil.COLON + name;
        this.redissonClient = redissonClient;
        this.workerId = calculateWorkerId();
        IExecutorsRepository.newSingleScheduledExecutor(name)
                .scheduleWithFixedDelay(this::updateTimestamps, DateMeasureConstants.ONE_MINUTES.toMillis(), DateMeasureConstants.FIVE_MINUTES.toMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    protected long getWorkerId() {
        return this.workerId;
    }

    private void updateTimestamps() {
        String hostAddr = ProjectContext.getContextInfo().getUip().getHostAddr();
        RMap<String, SnowFlakeDTO> workerIdMap = redissonClient.getMap(KEY);
        SnowFlakeDTO snowFlakeDTO = workerIdMap.get(hostAddr);
        snowFlakeDTO.setTimestamp(System.currentTimeMillis());
        workerIdMap.put(hostAddr, snowFlakeDTO);
    }

    private long calculateWorkerId() {
        String hostAddr = ProjectContext.getContextInfo().getUip().getHostAddr();
        RMap<String, SnowFlakeDTO> workerIdMap = redissonClient.getMap(KEY);
        SnowFlakeDTO snowFlakeDTO;
        if (workerIdMap.containsKey(hostAddr)) {
            // 已经存在直接返回workerId即可
            snowFlakeDTO = workerIdMap.get(hostAddr);
        } else {
            snowFlakeDTO = null;
            if (workerIdMap.isEmpty()) {
                // 为空直接从0开始新增即可
                snowFlakeDTO = new SnowFlakeDTO(System.currentTimeMillis(), 0);
            } else {
                // 计算workerId
                List<Integer> workerIds = workerIdMap.values().stream().map(SnowFlakeDTO::getWorkerId).sorted().toList();
                int calculateWorkerId = workerIds.getLast() + 1;
                if (calculateWorkerId > MAX_WORKER_ID) {
                    // check一下是否存在过期的workerId
                    long now = System.currentTimeMillis();
                    // 获取所有的workerId列表. 数据量不会很大, max = 1024.
                    for (Map.Entry<String, SnowFlakeDTO> entry : workerIdMap.entrySet()) {
                        String key = entry.getKey();
                        SnowFlakeDTO value = entry.getValue();
                        if (now - value.getTimestamp() > EXPIRED) {
                            // 超时了 替换该workerId
                            calculateWorkerId = value.getWorkerId();
                            workerIdMap.remove(key);
                            snowFlakeDTO = new SnowFlakeDTO(System.currentTimeMillis(), calculateWorkerId);
                            break;
                        }
                    }
                } else {
                    snowFlakeDTO = new SnowFlakeDTO(System.currentTimeMillis(), calculateWorkerId);
                }

                if (snowFlakeDTO == null) {
                    throw new IllegalArgumentException("WorkerID must gte 0 and lte 1023");
                }
            }
            workerIdMap.put(hostAddr, snowFlakeDTO);
        }
        return snowFlakeDTO.getWorkerId();

    }













}
