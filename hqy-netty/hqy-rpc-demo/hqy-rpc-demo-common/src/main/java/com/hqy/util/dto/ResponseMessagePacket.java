package com.hqy.util.dto;

import lombok.Data;

/**
 * @author qy
 * @description:
 * @project: hqy-parent
 * @create 2021-07-08 17:33
 */
@Data
public class ResponseMessagePacket extends BaseMessagePacket {

    /**
     * error code
     */
    private Long errorCode;

    /**
     * 消息描述
     */
    private String message;

    /**
     * 消息载荷
     */
    private Object payload;

}
