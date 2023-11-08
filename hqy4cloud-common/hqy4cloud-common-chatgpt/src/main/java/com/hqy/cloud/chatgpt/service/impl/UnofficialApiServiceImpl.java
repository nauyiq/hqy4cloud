/*
package com.hqy.cloud.chatgpt.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hqy.cloud.chatgpt.common.dto.ChatGptMessageReq;
import com.hqy.cloud.chatgpt.common.dto.UnofficialApiChatChunk;
import com.hqy.cloud.chatgpt.common.exception.OpenAiChatGptException;
import com.hqy.cloud.chatgpt.config.ChatGptConfigurationProperties;
import com.hqy.cloud.chatgpt.core.UnofficialApi;
import com.hqy.cloud.chatgpt.service.UnofficialApiService;
import com.hqy.cloud.common.base.lang.AuthConstants;
import com.hqy.cloud.foundation.id.DistributedIdGen;
import com.theokanning.openai.service.OpenAiService;
import io.reactivex.Flowable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Map;

import static com.hqy.cloud.chatgpt.common.lang.Constants.*;

*/
/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/27 15:04
 *//*

@Slf4j
@RequiredArgsConstructor
public record UnofficialApiServiceImpl(
        UnofficialApi unofficialApi, ChatGptConfigurationProperties properties) implements UnofficialApiService {
    private static final ObjectMapper MAPPER = OpenAiService.defaultObjectMapper();

    @Override
    public Flowable<UnofficialApiChatChunk> streamChat(ChatGptMessageReq req) throws OpenAiChatGptException {
        String accessToken = req.getAccessToken();
        if (StringUtils.isBlank(accessToken)) {
            throw new OpenAiChatGptException(OpenAiChatGptException.REQUEST_PARAMS_ERROR, "Access token should not be empty.");
        }

        final String authorization = AuthConstants.JWT_UPPERCASE_PREFIX + accessToken;
        String messageId = String.valueOf(DistributedIdGen.getSnowflakeId());
        String unofficialProxyUrl = properties.getUnofficialProxyUrl();
        Map<String, Object> body = buildRequestBody(req, messageId);

        Flowable.<UnofficialApiChatChunk>create(flowableEmitter ->
                unofficialApi.conversation(unofficialProxyUrl, body, authorization).enqueue(new ))

        return null;
    }

    private Map<String, Object> buildRequestBody(ChatGptMessageReq req, String messageId) {
        Map<String, Object> message = MapUtil.newHashMap(12);
        message.put(UNOFFICIAL_API_REQUEST_ID, messageId);
        message.put(UNOFFICIAL_API_REQUEST_AUTHOR, UnofficialApiChatChunk.Author.of(req.getRole().value(), req.getUsername()));
        message.put(UNOFFICIAL_API_REQUEST_CONTENT, MapUtil.builder()
                        .put(UNOFFICIAL_API_REQUEST_CONTENT_TYPE, UNOFFICIAL_API_REQUEST_TEXT)
                        .put(UNOFFICIAL_API_REQUEST_PARTS, Collections.singleton(req.getPrompt())).build());

        Map<String, Object> body = MapUtil.newHashMap(8);
        body.put(UNOFFICIAL_API_REQUEST_MESSAGES, Collections.singletonList(message));
        body.put(UNOFFICIAL_API_REQUEST_ACTION, UNOFFICIAL_API_REQUEST_NEXT);
        body.put(UNOFFICIAL_API_REQUEST_MODEL, UNOFFICIAL_API_REQUEST_MODEL_VALUE);

        String conversationId = StringUtils.isNotBlank(req.getConversationId()) ? req.getConversationId() : StrUtil.EMPTY;
        body.put(UNOFFICIAL_API_REQUEST_CONVERSATION_ID, conversationId);
        String parentId = StringUtils.isNotBlank(req.getParentMessageId()) ? req.getParentMessageId() : String.valueOf(DistributedIdGen.getSnowflakeId());
        message.put(UNOFFICIAL_API_REQUEST_PARENT_MESSAGE_ID, parentId);
        return body;
    }



}
*/
