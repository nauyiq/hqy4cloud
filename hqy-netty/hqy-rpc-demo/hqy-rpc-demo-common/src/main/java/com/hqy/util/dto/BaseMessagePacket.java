package com.hqy.util.dto;

import com.hqy.util.enums.MessageType;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author qy
 * @description: 基础Packet
 * @project: hqy-parent
 * @create 2021-07-08 17:30
 */
@Data
public abstract class BaseMessagePacket implements Serializable {


    /**
     * 魔数
     */
    private int magicNumber;

    /**
     * 版本号
     */
    private int version;

    /**
     * 流水号
     */
    private String serialNumber;

    /**
     * 消息类型
     */
    private MessageType messageType;

    /**
     * 附件 - K-V形式
     */
    private Map<String, String> attachments = new HashMap<>();

    /**
     * 添加附件
     */
    public void addAttachment(String key, String value) {
        attachments.put(key, value);
    }

}
