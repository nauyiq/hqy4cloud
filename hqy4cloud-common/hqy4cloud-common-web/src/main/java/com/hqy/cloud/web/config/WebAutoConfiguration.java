package com.hqy.cloud.web.config;

import com.hqy.cloud.auth.core.AuthorizationResourceRepository;
import com.hqy.cloud.web.filter.AuthUserFilter;
import com.hqy.cloud.web.filter.TokenFilter;
import com.hqy.cloud.web.support.HttpAuthenticationResourceScanner;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * @author qiyuan.hong
 * @date 2024/7/18
 */
@Configuration
@RequiredArgsConstructor
public class WebAutoConfiguration {


    @Bean
    public HttpAuthenticationResourceScanner httpAuthenticationResourceScanner(AuthorizationResourceRepository repository) {
        return new HttpAuthenticationResourceScanner(repository);
    }

    /**
     * 注册TOKEN 过滤器
     * @param redissonClient redisson客户端
     * @return               bean
     */
    @Bean
    public FilterRegistrationBean<TokenFilter> tokenFiler(AuthorizationResourceRepository repository, RedissonClient redissonClient) {
        FilterRegistrationBean<TokenFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TokenFilter(redissonClient));
        // 获取需要校验token的url pattern
        registrationBean.addUrlPatterns(repository.getIdentifierTokenUri().toArray(new String[0]));
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
