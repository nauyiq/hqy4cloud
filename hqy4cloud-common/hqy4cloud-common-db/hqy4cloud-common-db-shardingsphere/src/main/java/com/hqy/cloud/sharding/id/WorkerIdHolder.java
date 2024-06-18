package com.hqy.cloud.sharding.id;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.boot.CommandLineRunner;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/8
 */
@RequiredArgsConstructor
public class WorkerIdHolder implements CommandLineRunner {
    private final RedissonClient redissonClient;

    public static long workerId;

    @Override
    public void run(String... args) throws Exception {
        RAtomicLong atomicLong = redissonClient.getAtomicLong("workerId");
        workerId = atomicLong.incrementAndGet() % 32;
    }
}
