package com.hqy.cloud.chatgpt.core;

import okhttp3.ResponseBody;
import org.springframework.http.HttpHeaders;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.Map;

/**
 * 非官方请求open ai api.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/27 14:34
 */
public interface UnofficialApi {

    /**
     * 非官方请求open ai
     * @param url           非官方请求URL
     * @param body          消息体
     * @param authorization 认证请求头_access token
     * @return              Call result.
     */
    @POST
    @Streaming
    Call<ResponseBody> conversation(@Url String url, @Body Map<String, Object> body, @Header(HttpHeaders.AUTHORIZATION) String authorization);

}
