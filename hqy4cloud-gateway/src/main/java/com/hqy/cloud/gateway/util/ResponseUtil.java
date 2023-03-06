package com.hqy.cloud.gateway.util;

import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.bind.MessageResponse;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;

import java.nio.charset.StandardCharsets;

/**
 * @author qiyuan.hong
 * @date 2022-03-14 22:38
 */
public class ResponseUtil {

    private ResponseUtil() {}

    private static final Logger log = LoggerFactory.getLogger(ResponseUtil.class);

    /**
     * 返回数据缓冲区
     * @param code 业务码
     * @param response webflux response
     * @param status http响应码
     */
    public static DataBuffer outputBuffer(MessageResponse code, ServerHttpResponse response, HttpStatus status) {
        byte[] bytes = JsonUtil.toJson(code).getBytes(StandardCharsets.UTF_8);
        response.getHeaders().add(StringConstants.Headers.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.setStatusCode(status);
        return response.bufferFactory().wrap(bytes);
    }

    public static <T> DataBuffer outputBuffer(R<T> result, ServerHttpResponse response, HttpStatus status) {
        byte[] bytes = JsonUtil.toJson(result).getBytes(StandardCharsets.UTF_8);
        response.getHeaders().add(StringConstants.Headers.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.setStatusCode(status);
        return response.bufferFactory().wrap(bytes);
    }

}
