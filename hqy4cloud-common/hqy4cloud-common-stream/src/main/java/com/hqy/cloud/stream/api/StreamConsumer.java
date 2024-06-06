package com.hqy.cloud.stream.api;

import com.hqy.cloud.common.base.lang.DateMeasureConstants;
import com.hqy.cloud.util.concurrent.IExecutorService;
import lombok.*;

import java.time.Duration;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/5/6
 */
public interface StreamConsumer extends StreamService {

    /**
     * 启动消费者
     * @return 是否启动成功
     */
    boolean start();

    /**
     * 获取消费者配置
     * @return 消费者配置
     */
    Config getConfig();

    /**
     * 获取当前消费者的状态
     * @return 状态
     */
     State getState();

    /**
     * 获取注册到该消费者的监听器
     * @return 注册到该消费者的监听器 {@link StreamMessageListener}
     */
    StreamMessageListener getListener();


    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    class Config {

        /**
         * 订阅的主题
         */
        private String topic;

        /**
         * 消费者名称
         */
        private String name;

        /**
         * 消费者组
         */
        private String group;

        /**
         * 消费的超时时间
         */
        private Duration timeout;

        /**
         * 拉取消息的间隔时间，默认一秒钟； 只有消息模型是PULL的情况下才生效
         */
        private Duration pullInterval = DateMeasureConstants.ONE_SECONDS;

        /**
         * 拉取的长度，默认一条一条消费
         */
        private long batchSize = 1;

        /**
         * 是否手动ack消息
         */
        private boolean autoAck = true;

        /**
         * 消费者的模式
         */
        private Mode mode;

        /**
         * 消费时的线程池
         */
        private IExecutorService executorService;

        /**
         * 处理异常的handler
         */
        private ErrorHandler errorHandler;

    }

    enum Mode {

        /**
         * 消费消息的方式，主动去拉消息
         */
        PULL,

        /**
         * 消费消息的方式, 被动接收生产者推送消息
         */
        PUSH

    }

    enum State {

        /**
         * 初始化
         */
        INIT,

        /**
         * 已启动
         */
        STATED,

        /**
         * 已销毁
         */
        DESTROYED,


    }



}
