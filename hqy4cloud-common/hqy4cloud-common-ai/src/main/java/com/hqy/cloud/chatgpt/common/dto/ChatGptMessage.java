package com.hqy.cloud.chatgpt.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/27 11:38
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatGptMessage {

    private String parentMessageId;
    private String conversationId;

}
