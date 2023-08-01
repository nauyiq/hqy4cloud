package com.hqy.cloud.chatgpt.core;

import com.hqy.cloud.chatgpt.common.dto.ChatGptMessageReq;
import com.hqy.cloud.chatgpt.common.dto.ChatGptMessageResp;
import com.hqy.cloud.chatgpt.common.lang.ApiType;
import io.reactivex.Flowable;

/**
 * OpenAiChatGpt.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/27 10:55
 */
public interface OpenAiChatGpt {

    /**
     * stream chat with gpt
     * @param req  {@link  ChatGptMessageReq}
     * @param type api type
     * @return     result
     */
    Flowable<ChatGptMessageResp> streamChat(ChatGptMessageReq req, ApiType type);


}
