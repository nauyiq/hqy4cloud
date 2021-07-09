package com.hqy.util.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author qy
 * @description: 消息枚举类型
 * @project: hqy-parent
 * @create 2021-07-08 17:28
 */
@RequiredArgsConstructor
public enum MessageType {

    /**
     * 请求
     */
    REQUEST((byte) 1),

    /**
     * 响应
     */
    RESPONSE((byte) 2),

    /**
     * PING
     */
    PING((byte) 3),

    /**
     * PONG
     */
    PONG((byte) 4),

    /**
     * NULL
     */
    NULL((byte) 5),

    ;

    @Getter
    public final Byte type;

    public static MessageType fromValue(byte value) {
        for (MessageType type : MessageType.values()) {
            if (type.getType() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException(String.format("value = %s", value));
    }



}
