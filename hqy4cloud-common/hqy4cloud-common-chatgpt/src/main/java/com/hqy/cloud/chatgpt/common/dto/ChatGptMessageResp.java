package com.hqy.cloud.chatgpt.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ChatGptMessageResp
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/27 11:36
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ChatGptMessageResp extends ChatGptMessage {

    private String content;
    private String role;
    private LocalDateTime created;



}
