package com.hqy.cloud.mq.rocket.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.hqy.cloud.stream.common.StreamConstants;
import org.apache.rocketmq.common.message.Message;

/**
 * @author hongqy
 * @date 2025/5/26
 */
public class MessageUtil {

    public static <T> T getMessage(Message message, Class<T> clazz) {
        JSONObject jsonObject = JSON.parseObject(message.getBody());
        return JSON.parseObject(jsonObject.getString(StreamConstants.BODY), clazz);
    }

}
