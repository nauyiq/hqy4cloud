package com.hqy.cloud.chatgpt.core.support;

import com.hqy.cloud.chatgpt.common.dto.ChatGptMessageReq;
import com.hqy.cloud.chatgpt.common.dto.ChatGptMessageResp;
import com.hqy.cloud.chatgpt.common.lang.ApiType;
import com.hqy.cloud.chatgpt.core.OpenAiChatGpt;
import com.hqy.cloud.chatgpt.service.UnofficialApiService;
import com.theokanning.openai.service.OpenAiService;
import io.reactivex.Flowable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/27 14:53
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultOpenAiChatGpt implements OpenAiChatGpt {
    private final OpenAiService openAiService;
    private final UnofficialApiService unofficialApiService;

    @Override
    public Flowable<ChatGptMessageResp> streamChat(ChatGptMessageReq req, ApiType type) {





        return null;
    }
}
