package com.hqy.cloud.stream.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author qiyuan.hong
 * @date 2024/8/16
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageBody {

    /**
     * 幂等号
     */
    private String identifier;

    /**
     * 消息体
     */
    private String body;

    public static MessageBody create(String identifier, String body) {
        return new MessageBody(identifier, body);
    }


}
