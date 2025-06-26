package com.hqy.cloud.sharding.id;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/8
 */
@Setter
@RequiredArgsConstructor
public class WorkerIdHolder implements CommandLineRunner {
    private final RedissonClient redissonClient;

    @Value("${hqy4cloud.client.name:workerId}")
    private String clientName;

    public static long workerId;

    @Override
    public void run(String... args) throws Exception {
        RAtomicLong atomicLong = redissonClient.getAtomicLong(clientName);
        workerId = atomicLong.incrementAndGet() % 32;
    }
}
