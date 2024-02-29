package com.hqy.cloud.chatgpt.common.dto;

import cn.hutool.core.lang.UUID;
import com.hqy.cloud.chatgpt.common.lang.ChatGptModel;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

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

    private String model;

    private Double temperature;

    private Double topP;

    private ChatMessageRole role;

    private List<ChatMessage> history;

    public void setDefault() {
        this.model = StringUtils.isBlank(this.model) ? ChatGptModel.GTP_3_5_TURBO.name : this.model;
        this.temperature = this.temperature == null ? 0.8 : this.temperature;
        this.topP = this.topP == null ? 1.0 : this.topP;
        this.role = this.role == null ? ChatMessageRole.ASSISTANT : this.role;
        if (StringUtils.isBlank(super.getConversationId())) {
            super.setConversationId(UUID.fastUUID().toString());
        }
    }




}
