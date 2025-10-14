package com.hqy.cloud.util.concurrent.pc;

import cn.hutool.core.thread.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 过载阻塞式线程池
 * @author hongqy
 * @date 2025/9/11
 */
@Slf4j
public class BlockExecutor  {

    private BlockExecutor() {
        // 不带缓存线程池定制
        instance = new ThreadPoolExecutor(0, 120, 0L, TimeUnit.MILLISECONDS,
                new SynchronousQueue<>(true), new NamedThreadFactory("BlockExecutor", false), (r, executor) -> {
                    if (!executor.isShutdown()) {
                        try {
                            executor.getQueue().put(r);
                        } catch (Exception e) {
                            log.warn("过载阻塞式线程池执行出错，错误消息:{}", e.getMessage(), e);
                        }
                    }
                }
        );
    }

    private static volatile BlockExecutor blockExecutor;
    private static ThreadPoolExecutor instance;

    public static ThreadPoolExecutor getExecutor() {
        if (blockExecutor == null) {
            synchronized (BlockExecutor.class) {
                if (blockExecutor == null) {
                    blockExecutor = new BlockExecutor();
                }
            }
        }
        return instance;
    }

}

