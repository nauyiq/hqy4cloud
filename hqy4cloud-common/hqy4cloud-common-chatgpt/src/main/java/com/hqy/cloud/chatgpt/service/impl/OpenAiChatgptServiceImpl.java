package com.hqy.cloud.chatgpt.service.impl;

import com.hqy.account.service.RemoteAccountService;
import com.hqy.account.struct.AccountBaseInfoStruct;
import com.hqy.account.struct.ChatgptConfigStruct;
import com.hqy.cloud.chatgpt.common.dto.ChatGptMessageReq;
import com.hqy.cloud.chatgpt.common.exception.OpenAiChatGptException;
import com.hqy.cloud.chatgpt.config.ChatGptConfigurationProperties;
import com.hqy.cloud.chatgpt.service.OpenAiChatgptService;
import com.hqy.cloud.rpc.nacos.client.RPCClient;
import com.theokanning.openai.completion.chat.ChatCompletionChunk;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import io.reactivex.Flowable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.hqy.cloud.chatgpt.common.exception.OpenAiChatGptException.REQUEST_PARAMS_ERROR;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/4 13:40
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiChatgptServiceImpl implements OpenAiChatgptService {
    private final OpenAiService openAiService;
    private final ChatGptConfigurationProperties properties;

    @Override
    public Flowable<ChatCompletionChunk> streamChatCompletion(Long userId, ChatGptMessageReq req) throws OpenAiChatGptException {
        if (userId == null || StringUtils.isBlank(req.getPrompt())) {
            throw new OpenAiChatGptException(REQUEST_PARAMS_ERROR);
        }
        RemoteAccountService service = RPCClient.getRemoteService(RemoteAccountService.class);
        AccountBaseInfoStruct struct = service.getAccountBaseInfo(userId);
        if (struct == null) {
            log.error("Not found account info by id={}.", userId);
            return null;
        }
        ChatgptConfigStruct chatgptConfig = struct.chatgptConfig;

        String apiKey = null;
        boolean usingSystemApiKey = false;
        if (StringUtils.isNotBlank(chatgptConfig.apiKey)) {
            apiKey = chatgptConfig.apiKey;
        } else if (chatgptConfig.time != null && chatgptConfig.time > 0){
            apiKey = properties.getApiKey();
            usingSystemApiKey = true;
        }
        if (StringUtils.isBlank(apiKey)) {
            log.warn("Not qualification to request openai, userId = {}.", userId);
            return null;
        }

        boolean result = true;
        ChatCompletionRequest request = buildRequest(apiKey, chatgptConfig.maxTokens, req);
        try {
            return openAiService.streamChatCompletion(request);
        } catch (Throwable cause) {
            log.error(cause.getMessage(), cause);
            result = false;
            return null;
        } finally {
            if (usingSystemApiKey && result) {
                //TODO api time--
            }
        }

    }


    private ChatCompletionRequest buildRequest(String apiKey, int maxTokens, ChatGptMessageReq req) {
        req.setDefault();
        return ChatCompletionRequest.builder()
                .model(req.getModel())
                .messages(buildMessage(req))
                .temperature(req.getTemperature())
                .topP(req.getTopP())
                .maxTokens(maxTokens).build();
    }

    public List<ChatMessage> buildMessage(ChatGptMessageReq req) {
        List<ChatMessage> messages = new ArrayList<>();
        // system message
        if (StringUtils.isNotBlank(req.getSystemMessage())) {
            ChatMessage msg = new ChatMessage();
            msg.setRole(ChatMessageRole.SYSTEM.value());
            msg.setContent(req.getSystemMessage());
            messages.add(msg);
        }

        //history message
        if (CollectionUtils.isNotEmpty(req.getHistory())) {
            messages.addAll(req.getHistory());
        } else {
            req.setParentMessageId(null);
        }

        // user send message
        ChatMessage latestMsg = new ChatMessage();
        latestMsg.setRole(ChatMessageRole.USER.value());
        latestMsg.setContent(req.getPrompt());
        messages.add(latestMsg);
        return messages;
    }
}
