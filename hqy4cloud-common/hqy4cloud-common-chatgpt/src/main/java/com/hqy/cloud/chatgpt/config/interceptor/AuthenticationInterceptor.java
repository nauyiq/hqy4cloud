package com.hqy.cloud.chatgpt.config.interceptor;

import com.hqy.cloud.common.base.lang.AuthConstants;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;

import java.io.IOException;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/7/27 13:35
 */
public record AuthenticationInterceptor(String accessSecret) implements Interceptor {

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        if (StringUtils.isBlank(request.header(HttpHeaders.AUTHORIZATION))) {
            request = request.newBuilder().header(HttpHeaders.AUTHORIZATION,
                    AuthConstants.JWT_UPPERCASE_PREFIX + accessSecret).build();
        }
        return chain.proceed(request);
    }
}
