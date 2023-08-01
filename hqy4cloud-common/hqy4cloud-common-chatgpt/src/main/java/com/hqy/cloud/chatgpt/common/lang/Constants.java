package com.hqy.cloud.chatgpt.common.lang;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/27 10:26
 */
public interface Constants {

    String DEFAULT_OPEN_AI_BASE_URL = "https://api.openai.com";
    String DEFAULT_UNOFFICIAL_PROXY_URL = "https://bypass.churchless.tech/api/conversation";
    long DEFAULT_REQUEST_TIMEOUT = 30000;


    String UNOFFICIAL_API_REQUEST_ID = "id";
    String UNOFFICIAL_API_REQUEST_AUTHOR = "action";
    String UNOFFICIAL_API_REQUEST_CONTENT_TYPE= "content_type";
    String UNOFFICIAL_API_REQUEST_TEXT= "text";
    String UNOFFICIAL_API_REQUEST_PARTS= "parts";
    String UNOFFICIAL_API_REQUEST_CONTENT= "content";
    String UNOFFICIAL_API_REQUEST_MESSAGES= "messages";
    String UNOFFICIAL_API_REQUEST_ACTION= "action";
    String UNOFFICIAL_API_REQUEST_MODEL= "model";
    String UNOFFICIAL_API_REQUEST_MODEL_VALUE= "text-davinci-002-render-sha";
    String UNOFFICIAL_API_REQUEST_NEXT= "next";
    String UNOFFICIAL_API_REQUEST_CONVERSATION_ID= "conversation_id";
    String UNOFFICIAL_API_REQUEST_PARENT_MESSAGE_ID= "parent_message_id";


}
