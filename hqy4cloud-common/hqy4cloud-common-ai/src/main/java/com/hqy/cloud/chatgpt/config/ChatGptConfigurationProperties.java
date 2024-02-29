package com.hqy.cloud.chatgpt.config;

import cn.hutool.core.util.ObjectUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import static com.hqy.cloud.chatgpt.common.lang.ChatGptModel.GTP_3_5_TURBO;
import static com.hqy.cloud.chatgpt.common.lang.Constants.*;

/**
 * chat-gtp配置属性类
 * 定义请求的url, 模型, api-key等等
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/27 10:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatGptConfigurationProperties {

    /**
     * OpenAI API KEY
     */
    private String apiKey;

    /**
     * OpenAI API Model - https://platform.openai.com/docs/models
     */
    private String apiModel = GTP_3_5_TURBO.name;

    /**
     * OpenAI API Base URL - https://api.openai.com
     */
    private String apiBaseUrl = DEFAULT_OPEN_AI_BASE_URL;

    /**
     * API request timeout, ms
     */
    private Long apiRequestTimeout = DEFAULT_REQUEST_TIMEOUT;

    /**
     * Uses an unofficial proxy server to access ChatGPT's backend API
     */
    private String unofficialProxyUrl = DEFAULT_UNOFFICIAL_PROXY_URL;

    /**
     * Socks Proxy
     */
    private SocksProxy socksProxy;

    /**
     * HTTP Proxy
     */
    private HttpProxy httpProxy;

    @Data
    public static class SocksProxy {
        private String host;
        private Integer port;
        private String username;
        private String password;

        public boolean isAvailable() {
            return ObjectUtil.isAllNotEmpty(host, port);
        }
    }

    @Data
    public static class HttpProxy {
        private String host;
        private Integer port;

        public boolean isAvailable() {
            return ObjectUtil.isAllNotEmpty(host, port);
        }
    }









}
