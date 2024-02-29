package com.hqy.cloud.chatgpt.service;

import com.hqy.cloud.chatgpt.common.dto.ChatGptMessageReq;
import com.hqy.cloud.chatgpt.common.dto.UnofficialApiChatChunk;
import com.hqy.cloud.chatgpt.common.exception.OpenAiChatGptException;
import io.reactivex.Flowable;

/**
 * 非官方请求open ai service.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/27 14:20
 */
public interface UnofficialApiService {

    /**
     * Unofficial steam chat with gpt
     * @param req {@link ChatGptMessageReq}
     * @return    result
     * @throws OpenAiChatGptException ex.
     */
    Flowable<UnofficialApiChatChunk> streamChat(ChatGptMessageReq req) throws OpenAiChatGptException;


}
