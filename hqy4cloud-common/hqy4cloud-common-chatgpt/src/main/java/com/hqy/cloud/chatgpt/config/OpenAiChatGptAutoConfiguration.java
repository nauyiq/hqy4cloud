package com.hqy.cloud.chatgpt.config;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.chatgpt.config.interceptor.AuthenticationInterceptor;
import com.hqy.cloud.chatgpt.config.interceptor.ProxyAuthenticator;
import com.hqy.cloud.chatgpt.core.UnofficialApi;
import com.theokanning.openai.OpenAiApi;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import static com.hqy.cloud.chatgpt.common.lang.Constants.DEFAULT_UNOFFICIAL_PROXY_URL;

/**
 * OpenAiChatGptAutoConfiguration.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/27 13:25
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class OpenAiChatGptAutoConfiguration {
    private final ChatGptConfigurationProperties properties;

    @ConditionalOnMissingBean
    @Bean(name = "apiOkHttpClient")
    public OkHttpClient apiOkHttpClient() {
        OkHttpClient.Builder builder = getBuilder();
        builder.addInterceptor(new AuthenticationInterceptor(properties.getApiKey()));
        //设置代理
        settingProxy(builder);
        return builder.build();
    }

    @ConditionalOnMissingBean
    @Bean(name = "unofficialProxyOkHttpClient")
    public OkHttpClient unofficialProxyOkHttpClient() {
        OkHttpClient.Builder builder = getBuilder();
        //设置代理
        settingProxy(builder);
        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean
    public OpenAiService openAiService(OkHttpClient apiOkHttpClient) {
        Retrofit retrofit = OpenAiService.defaultRetrofit(apiOkHttpClient, OpenAiService.defaultObjectMapper()).newBuilder()
                .baseUrl(properties.getApiBaseUrl()).build();
        return new OpenAiService(retrofit.create(OpenAiApi.class), apiOkHttpClient.dispatcher().executorService());
    }

    @Bean
    @ConditionalOnMissingBean
    public UnofficialApi unofficialApi(OkHttpClient unofficialProxyOkHttpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DEFAULT_UNOFFICIAL_PROXY_URL + StrUtil.SLASH)
                .client(unofficialProxyOkHttpClient)
                .addConverterFactory(JacksonConverterFactory.create(OpenAiService.defaultObjectMapper()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(UnofficialApi.class);
    }



    @NotNull
    private OkHttpClient.Builder getBuilder() {
        ConnectionPool connectionPool = new ConnectionPool(Runtime.getRuntime().availableProcessors(), 1, TimeUnit.MINUTES);
        return new OkHttpClient.Builder()
                .connectionPool(connectionPool)
                .readTimeout(properties.getApiRequestTimeout(), TimeUnit.MILLISECONDS);
    }

    private void settingProxy(OkHttpClient.Builder builder) {
        Proxy proxy = null;

        // HTTP代理
        ChatGptConfigurationProperties.HttpProxy httpProxy = properties.getHttpProxy();
        if (httpProxy != null && httpProxy.isAvailable()) {
            proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(httpProxy.getHost(), httpProxy.getPort()));
        }

        // SOCKS代理
        ChatGptConfigurationProperties.SocksProxy socksProxy = properties.getSocksProxy();
        if (socksProxy != null && socksProxy.isAvailable()) {
            proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(socksProxy.getHost(), socksProxy.getPort()));
            if (!StringUtils.isAllBlank(socksProxy.getPassword(), socksProxy.getUsername())) {
                Authenticator.setDefault(new ProxyAuthenticator(socksProxy.getUsername(), socksProxy.getPassword()));
            }
        }

        if (proxy != null) {
            builder.proxy(proxy);
            log.info("OkHttpClient using proxy: {}.", proxy);
        }

    }


}
