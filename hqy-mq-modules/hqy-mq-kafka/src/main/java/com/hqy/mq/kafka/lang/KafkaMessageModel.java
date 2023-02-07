package com.hqy.mq.kafka.lang;

import com.hqy.mq.common.MessageModel;

/**
 * KafkaMessageModel.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/6 14:34
 */
public interface KafkaMessageModel extends MessageModel {

    /**
     * 获取投递的主题
     * @return 投递主题
     */
    String topic();

    /**
     * 设置投递消息key
     * @return 消息key
     */
    default String key() {
        return null;
    }

    /**
     * 指定投递到哪个topic下的partition
     * Partition 和 key 都未指定，则使用kafka默认的分区策略，轮询选出一个 Partition；
     * @return Partition
     */
    default Integer partition() { return null; }

    /**
     * 提交延迟
     * @return 提交延迟
     */
    default Long timestamp() {return null; }

}
