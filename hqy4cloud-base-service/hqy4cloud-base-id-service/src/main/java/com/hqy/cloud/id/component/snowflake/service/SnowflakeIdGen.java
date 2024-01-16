package com.hqy.cloud.id.component.snowflake.service;

import com.google.common.base.Preconditions;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.id.component.snowflake.core.support.SnowflakeRedisHolder;
import com.hqy.cloud.id.component.snowflake.exception.InitWorkerIdException;
import com.hqy.cloud.id.service.IdGen;
import com.hqy.cloud.id.struct.ResultStruct;
import com.hqy.cloud.registry.common.context.Environment;
import com.hqy.cloud.registry.context.ProjectContext;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;

import java.util.Random;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/22 13:45
 */
@Slf4j
public class SnowflakeIdGen implements IdGen {

    /**
     * 机器id所占的位数
     */
    private final long workerIdBits = 10L;

    /**
     * 开始时间戳
     */
    private final long twepoch;

    /**
     * 工作机器ID
     */
    private final long workerId;

    /**
     * 毫秒内序列
     */
    private long sequence = 0L;

    /**
     * 上次生成ID的时间截
     */
    private long lastTimestamp = -1L;


    private static final Random RANDOM = new Random();


    public SnowflakeIdGen(long workerId) {
        //Thu Nov 04 2010 09:42:54 GMT+0800 (中国标准时间)
        this.twepoch = 1288834974657L;
        this.workerId = workerId;
    }

    public SnowflakeIdGen(RedissonClient redissonClient) {
        //Thu Nov 04 2010 09:42:54 GMT+0800 (中国标准时间)
        this(1288834974657L, redissonClient);
    }


    public SnowflakeIdGen(long twepoch, RedissonClient redissonClient) {
        this.twepoch = twepoch;
        Preconditions.checkArgument(timeGen() > twepoch, "Snowflake not support twepoch gt currentTime");
        SnowflakeRedisHolder redisHolder = new SnowflakeRedisHolder(ProjectContext.getEnvironment(), redissonClient);
        boolean initFlag = redisHolder.initWorkerId(MicroServiceConstants.ID_SERVICE);
        if (initFlag) {
            this.workerId = redisHolder.getWorkerId();
        } else {
            throw new InitWorkerIdException("Snowflake id gen is not init ok.");
        }

        //最大能够分配的workerId = 1023
        long maxWorkerId = ~(-1L << workerIdBits);
        Preconditions.checkArgument(workerId >= 0 && workerId <= maxWorkerId, "WorkerId must gte 0 and lte 1023");
    }

    @Override
    public ResultStruct get(String key) {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            long offset = lastTimestamp - timestamp;
            if (offset <= 5) {
                try {
                    wait(offset << 1);
                    timestamp = timeGen();
                    if (timestamp < lastTimestamp) {
                        return new ResultStruct(-1, false);
                    }
                } catch (InterruptedException e) {
                    log.error("wait interrupted");
                    return new ResultStruct(-2, false);
                }
            } else {
                return new ResultStruct(-3, false);
            }
        }

        // 如果是同一时间生成的，则进行毫秒内序列 序列在id中占的位数
        long sequenceBits = 12L;
        if (lastTimestamp == timestamp) {
            // 生成序列的掩码
            long sequenceMask = ~(-1L << sequenceBits);
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                //seq 为0的时候表示是下一毫秒时间开始对seq做随机
                sequence = RANDOM.nextInt(100);
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            //如果是新的ms开始
            sequence = RANDOM.nextInt(100);
        }
        //机器ID向左移sequenceBits位
        long workerIdShift = sequenceBits;
        lastTimestamp = timestamp;
        // 时间截向左移(sequenceBits + workerIdBits)位
        long timestampLeftShift = sequenceBits + workerIdBits;
        long id = ((timestamp - twepoch) << timestampLeftShift) | (workerId << workerIdShift) | sequence;
        return new ResultStruct(id, true);

    }


    @Override
    public boolean init() {
        return true;
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

}
