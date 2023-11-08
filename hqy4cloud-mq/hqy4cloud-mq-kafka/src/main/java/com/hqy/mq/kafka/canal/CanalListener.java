package com.hqy.mq.kafka.canal;

import org.springframework.kafka.listener.BatchAcknowledgingMessageListener;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/18 16:31
 */
public interface CanalListener extends BatchAcknowledgingMessageListener<String, String> {

}
