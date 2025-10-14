package com.hqy.cloud.util.concurrent.pc;

import cn.hutool.core.lang.Assert;
import com.google.common.base.Stopwatch;
import com.hqy.cloud.util.concurrent.IExecutorsRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.concurrent.*;

/**
 * 生产者消费者模板类
 * @author hongqy
 * @date 2025/7/17
 */
@Slf4j
public class PcTemplate<T> {
    private static final int DEFAULT_CONSUMER_THREAD_NUM = Runtime.getRuntime().availableProcessors() * 2   ;
    private static final int DEFAULT_CONSUMER_TIMEOUT_SECONDS = 5;
    private static final int DEFAULT_PRODUCER_THREAD_NUM = 1;
    private static final int DEFAULT_QUEUE_CAPACITY = 2000;
    private static final ExecutorService DEFAULT_EXECUTOR_SERVICE = BlockExecutor.getExecutor();

    /**
     * 消费者
     */
    private Consumer<T> consumer;

    /**
     * 消费者线程数
     */
    private int consumerThreadNum;

    /**
     * 消费者超时时间
     */
    private int consumerTimeoutSeconds;


    /**
     * 生产者
     */
    private Producer<T> producer;

    /**
     * 生产者线程数
     */
    private int producerThreadNum;

    /**
     * 运行线程池
     */
    private ExecutorService executorService;

    /**
     * 消息队列
     */
    private BlockingQueue<T> queue;

    /**
     * 异常时退出线程
     */
    private Boolean breakOnException;


    public void runAndWait() {
        Assert.notNull(consumer, "消费者不能为空");
        Assert.notNull(producer, "生产者不能为空");

        log.info("生产者消费者模板启动...");
        Stopwatch stopwatch = Stopwatch.createStarted();
        CountDownLatch countDownLatch = new CountDownLatch(consumerThreadNum + producerThreadNum);
        // 构建生产者线程
        for (int i = 0; i < producerThreadNum; i++) {
            this.executorService.execute(new ProducerWrapper(countDownLatch));
        }
        // 构建消费者线程
        for (int i = 0; i < consumerThreadNum; i++) {
            this.executorService.execute(new ConsumerWrapper(countDownLatch));
        }

        try {
            // 阻塞主线程
            countDownLatch.await();
        } catch (Exception e) {
           log.error(e.getMessage(), e);
        }

        log.info("生产者消费者模板退出， 耗时{}ms.", stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }



    @AllArgsConstructor
    public class ProducerWrapper implements Runnable {
        private final CountDownLatch countDownLatch;
        @Override
        public void run() {
            log.info("生产者模板开始运行");
            try {
                while (true) {
                    try {
                        List<T> data = producer.generate();
                        if (CollectionUtils.isNotEmpty(data)) {
                            queue.addAll(data);
                        } else {
                            break;
                        }
                    } catch (Exception e) {
                        log.error("生产者执行时发生异常, cause:{}", e.getMessage(), e);
                        if (breakOnException) {
                            // 发生异常时结束消费者线程
                            log.info("生产者模板运行时发生异常, 生产者退出...");
                            break;
                        }
                    }
                }
            }  finally {
                countDownLatch.countDown();
                log.info("生产者模板退出");
            }
        }
    }



    @AllArgsConstructor
    public class ConsumerWrapper implements Runnable {
        private final CountDownLatch countDownLatch;

        @Override
        public void run() {
            log.info("消费者模板开始运行");
            try {
                while (true) {
                    try {
                        T data = queue.poll(consumerTimeoutSeconds, TimeUnit.SECONDS);
                        if (data == null) {
                            // 队列元素为空 则线程退出
                            break;
                        }
                        // 核心逻辑执行
                        consumer.accept(data);
                    } catch (Exception e) {
                        log.error("消费者执行时发生异常, cause:{}", e.getMessage(), e);
                        if (breakOnException) {
                            // 发生异常时结束消费者小城
                            log.info("消费者模板运行时发生异常, 消费者退出...");
                            break;
                        }
                    }
                }
            } finally {
                countDownLatch.countDown();
                log.info("消费者模板退出");
            }
        }
    }



    public static class PcTemplateBuilder<T> {
        private Consumer<T> consumer;
        private Integer consumerThreadNum;
        private Integer consumerTimeoutSeconds;
        private Producer<T> producer;
        private Integer producerThreadNum;
        private ExecutorService executorService;
        private Boolean breakOnException;
        private Integer capacity;

        public PcTemplateBuilder<T> consumer(Consumer<T> consumer) {
            this.consumer = consumer;
            return this;
        }

        public PcTemplateBuilder<T> consumerThreadNum(int consumerThreadNum) {
            this.consumerThreadNum = consumerThreadNum;
            return this;
        }

        public PcTemplateBuilder<T> consumerTimeoutSeconds(int consumerTimeoutSeconds) {
            this.consumerTimeoutSeconds = consumerTimeoutSeconds;
            return this;
        }

        public PcTemplateBuilder<T> producer(Producer<T> producer) {
            this.producer = producer;
            return this;
        }

        public PcTemplateBuilder<T> producerThreadNum(int producerThreadNum) {
            this.producerThreadNum = producerThreadNum;
            return this;
        }

        public PcTemplateBuilder<T> executorService(ExecutorService executorService) {
            this.executorService = executorService;
            return this;
        }

        public PcTemplateBuilder<T> capacity(int capacity) {
            this.capacity = capacity;
            return this;
        }

        public PcTemplateBuilder<T> breakOnException(Boolean breakOnException) {
            this.breakOnException = breakOnException;
            return this;
        }

        public PcTemplate<T> build() {
            PcTemplate<T> pcTemplate = new PcTemplate<>();
            pcTemplate.consumer = consumer;
            pcTemplate.consumerThreadNum = consumerThreadNum == null ? DEFAULT_CONSUMER_THREAD_NUM : consumerThreadNum;
            pcTemplate.consumerTimeoutSeconds = consumerTimeoutSeconds == null ? DEFAULT_CONSUMER_TIMEOUT_SECONDS : consumerTimeoutSeconds;
            pcTemplate.producer = producer;
            pcTemplate.producerThreadNum = producerThreadNum == null ? DEFAULT_PRODUCER_THREAD_NUM : producerThreadNum;
            if (this.capacity == null) {
                this.capacity = DEFAULT_QUEUE_CAPACITY;
            }
            pcTemplate.queue = new LinkedBlockingDeque<>(capacity);
            pcTemplate.executorService = executorService == null ? DEFAULT_EXECUTOR_SERVICE : executorService;
            pcTemplate.breakOnException = breakOnException == null ? Boolean.FALSE : breakOnException;
            return pcTemplate;
        }


    }


}
