package com.hqy.cloud.mq.rocket.lang;

import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.stream.api.MessageId;
import com.hqy.cloud.stream.common.AbstractStreamMessage;
import org.apache.commons.lang3.StringUtils;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/5/29
 */
public class RocketmqMessage extends AbstractStreamMessage<String, Object> {

    private final String topic;
    private final String tags;

    public RocketmqMessage(String topic, Object value) {
        this(null, topic, null, value);
    }

    public RocketmqMessage(String topic, String tags, Object value) {
        this(null, topic, tags, value);
    }

    public RocketmqMessage(MessageId<String> messageId, String topic, String tags, Object value) {
        super(messageId, value);
        this.topic = topic;
        this.tags = tags;
    }


    @Override
    public String getTopic() {
        return this.topic;
    }

    public String getTags() {
        return tags;
    }

    public String getDestination() {
        if (StringUtils.isNotBlank(this.tags)) {
            return this.topic + StringConstants.Symbol.COLON + this.tags;
        }
        return this.topic;
    }




}
