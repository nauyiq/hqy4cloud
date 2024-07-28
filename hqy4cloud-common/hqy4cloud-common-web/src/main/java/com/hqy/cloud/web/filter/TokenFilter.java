package com.hqy.cloud.web.filter;

import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.web.utils.TokenUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;

import java.io.IOException;
import java.util.List;

/**
 * @author qiyuan.hong
 * @date 2024/7/18
 */
@Slf4j
@RequiredArgsConstructor
public class TokenFilter implements Filter {

    private final RedissonClient redissonClient;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            // 从请求中获取Token
            String token = httpRequest.getHeader("token");
            if (StringUtils.isBlank(token) || StringConstants.NULL.equals(token) || StringConstants.UNDEFINED.equals(token)) {
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.getWriter().write("No Token Found ...");
                log.error("No token found in header , pls check!");
                return;
            }

            // 检查TOKEN是否有限.
            boolean isValid = checkTokenValidity(token);
            if (!isValid) {
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.getWriter().write("Invalid or expired token");
                log.error("token validate failed , pls check!");
                return;
            }
            // Token有效，继续执行其他过滤器链
            filterChain.doFilter(request, response);
        } finally {
            TokenUtil.remove();
        }
    }

    private boolean checkTokenValidity(String token) {
        // 获取并删除
        String luaScript = """
                local value = redis.call('GET', KEYS[1])
                redis.call('DEL', KEYS[1])
                return value""";

        String result = redissonClient.getScript()
                .eval(RScript.Mode.READ_WRITE, luaScript, RScript.ReturnType.STATUS, List.of(token));
        TokenUtil.set(token);
        return StringUtils.isNotBlank(result);
    }
}
