package com.hqy.cloud.chatgpt.service;

import com.hqy.cloud.chatgpt.common.dto.ChatGptMessageReq;
import com.hqy.cloud.chatgpt.common.exception.OpenAiChatGptException;
import com.theokanning.openai.completion.chat.ChatCompletionChunk;
import io.reactivex.Flowable;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/4 13:36
 */
public interface OpenAiChatgptService {

    /**
     * stream chat with chatgpt.
     * @param userId  用户id
     * @param req     request
     * @return        result
     * @throws OpenAiChatGptException 异常
     */
    Flowable<ChatCompletionChunk> streamChatCompletion(Long userId, ChatGptMessageReq req) throws OpenAiChatGptException;

}
