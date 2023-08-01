package com.hqy.cloud.chatgpt.common.dto;

import com.theokanning.openai.completion.chat.ChatMessageRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * ChatGptMessageReqDTO
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/27 11:21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ChatGptMessageReq extends ChatGptMessage {

    @NotEmpty(message = "message prompt should not be empty.")
    private String prompt;

    private String systemMessage;

    private Double temperature;

    private Double topP;

    private String accessToken;

    private String username;

    private ChatMessageRole role;


}
