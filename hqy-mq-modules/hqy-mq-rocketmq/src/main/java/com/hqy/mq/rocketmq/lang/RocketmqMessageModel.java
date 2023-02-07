package com.hqy.mq.rocketmq.lang;

import com.hqy.mq.common.MessageModel;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/7 16:20
 */
public interface RocketmqMessageModel extends MessageModel {

    /**
     * 获取主题
     * @return 主题
     */
    String topic();


}
