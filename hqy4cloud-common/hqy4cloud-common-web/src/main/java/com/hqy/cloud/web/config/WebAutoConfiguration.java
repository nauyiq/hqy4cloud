package com.hqy.cloud.web.config;

import com.hqy.cloud.web.filter.AuthUserFilter;
import com.hqy.cloud.web.filter.TokenFilter;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;

import java.util.List;

/**
 * @author qiyuan.hong
 * @date 2024/7/18
 */
@Configuration
@RequiredArgsConstructor
public class WebAutoConfiguration {

    private final Environment environment;
    private static final String TOKEN_URL_PATTERNS_KEY = "hqy4cloud.token.urls";
    private static final List<String> DEFAULT_TOKEN_URL_PATTERNS = List.of("/nft/trade/buy");

    /**
     * 注册TOKEN 过滤器
     * @param redissonClient redisson客户端
     * @return               bean
     */
    @Bean
    @SuppressWarnings("unchecked")
    public FilterRegistrationBean<TokenFilter> tokenFiler(RedissonClient redissonClient) {
        FilterRegistrationBean<TokenFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TokenFilter(redissonClient));
        // 获取需要校验token的url pattern
        List<String> urls = environment.getProperty(TOKEN_URL_PATTERNS_KEY, List.class, DEFAULT_TOKEN_URL_PATTERNS);
        registrationBean.addUrlPatterns(urls.toArray(new String[0]));
        registrationBean.setOrder(10);
        return registrationBean;
    }

    /**
     * 注册AUTHUser 过滤器
     * @return
     */
    @Bean
    public FilterRegistrationBean<AuthUserFilter> authUserFilter() {
        FilterRegistrationBean<AuthUserFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new AuthUserFilter());
        bean.addUrlPatterns("/**");
        bean.setOrder(Ordered.LOWEST_PRECEDENCE);
        return bean;
    }


}
