package com.hqy.cloud.stream.api;

import com.hqy.cloud.stream.core.StreamResult;
import com.hqy.cloud.stream.core.CompletableFutureResult;
import com.hqy.cloud.util.concurrent.IExecutorService;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/4/24
 */
public interface StreamProducer<R> extends StreamService {

    /**
     * 获取该生产者的配置.
     * @return 生产者配置
     */
    Config getConfig();

    /**
     * 同步发消息
     * @param streamMessage 抽象出来的消息体
     * @return              结果
     */
    default <K extends Comparable<K>, V> StreamResult<R> syncSend(StreamMessage<K, V> streamMessage) {
        return syncSend(streamMessage, null);
    }

    /**
     * 同步发消息
     * @param streamMessage 消息
     * @param callback      回调函数
     * @return              发送结果
     */
    <K extends Comparable<K>, V> StreamResult<R> syncSend(StreamMessage<K, V> streamMessage, StreamCallback<R> callback);

    /**
     * oneway发消息
     * @param streamMessage 消息
     */
    default <K extends Comparable<K>, V> void onewaySend(StreamMessage<K, V> streamMessage) {
        onewaySend(streamMessage, null);
    }

    /**
     * oneway发消息
     * @param streamMessage 消息
     * @param callback      回到
     */
    <K extends Comparable<K>, V> void onewaySend(StreamMessage<K, V> streamMessage, StreamCallback<R> callback);


    /**
     * 异步发送消息，并且返回CompletableFuture
     * @param streamMessage 消息
     * @return        future
     */
    default  <K extends Comparable<K>, V> CompletableFutureResult<R> asyncSend(StreamMessage<K, V> streamMessage) {
        return asyncSend(streamMessage, null);
    }

    /**
     * 异步发送消息，并且返回CompletableFuture
     * @param streamMessage 消息
     * @param callback      回调
     * @return              future
     */
    <K extends Comparable<K>, V> CompletableFutureResult<R> asyncSend(StreamMessage<K, V> streamMessage, StreamCallback<R> callback);


    @Builder
    @AllArgsConstructor
    class Config {

        /**
         * 异步消息使用到的线程池.
         */
        private IExecutorService executorService;

        /**
         * 原生api是否支持异步
         */
        private boolean supportAsyncApi = true;

        /**
         * 是否允许发送空消息
         */
        private boolean supportSendEmptyMessage = true;


        public Config() {
        }

        public Config(IExecutorService executorService) {
            this.executorService = executorService;
        }

        public static Config of(IExecutorService executorService) {
            return new Config(executorService);
        }


        public IExecutorService getExecutorService() {
            return executorService;
        }

        public void setExecutorService(IExecutorService executorService) {
            this.executorService = executorService;
        }

        public boolean isSupportAsyncApi() {
            return supportAsyncApi;
        }

        public void setSupportAsyncApi(boolean supportAsyncApi) {
            this.supportAsyncApi = supportAsyncApi;
        }

        public boolean isSupportSendEmptyMessage() {
            return supportSendEmptyMessage;
        }

        public void setSupportSendEmptyMessage(boolean supportSendEmptyMessage) {
            this.supportSendEmptyMessage = supportSendEmptyMessage;
        }
    }


}
