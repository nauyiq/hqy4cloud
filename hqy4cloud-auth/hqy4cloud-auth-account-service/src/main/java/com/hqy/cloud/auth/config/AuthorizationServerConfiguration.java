package com.hqy.cloud.auth.config;

import com.hqy.cloud.auth.security.common.SecurityConstants;
import com.hqy.cloud.auth.security.core.RedisOAuth2AuthorizationService;
import com.hqy.cloud.auth.server.DefaultRegisteredClientRepository;
import com.hqy.cloud.auth.service.security.support.CustomerUserDetailServiceImpl;
import com.hqy.cloud.auth.service.security.support.RedisOAuth2AuthorizationConsentServiceImpl;
import com.hqy.cloud.auth.account.service.AccountService;
import com.hqy.cloud.auth.account.service.SysOauthClientService;
import com.hqy.cloud.auth.support.core.DefaultDaoAuthenticationProvider;
import com.hqy.cloud.auth.support.core.DefaultOauth2TokenCustomizer;
import com.hqy.cloud.auth.support.core.FormIdentityLoginConfigurer;
import com.hqy.cloud.auth.support.core.email.Oauth2ResourceOwnerEmailAuthenticationConverter;
import com.hqy.cloud.auth.support.core.email.Oauth2ResourceOwnerEmailAuthenticationProvider;
import com.hqy.cloud.auth.support.core.password.Oauth2ResourceOwnerPasswordAuthenticationConverter;
import com.hqy.cloud.auth.support.core.password.Oauth2ResourceOwnerPasswordAuthenticationProvider;
import com.hqy.cloud.auth.support.handler.DefaultAuthenticationFailureHandler;
import com.hqy.cloud.auth.support.handler.DefaultAuthenticationSuccessHandler;
import com.hqy.cloud.auth.support.server.DefaultOauth2AccessTokenGenerator;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2RefreshTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.web.authentication.*;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Arrays;

/**
 * 资源服务器配置.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/24 11:30
 */
@Configuration
@SuppressWarnings({"rawtypes", "unchecked"})
public class AuthorizationServerConfiguration {

    @Bean
    public RegisteredClientRepository registeredClientRepository(SysOauthClientService sysOauthClientService) {
        return new DefaultRegisteredClientRepository(sysOauthClientService);
    }
    @Bean
    public UserDetailsService userDetailsService(AccountService accountService) {
        return new CustomerUserDetailServiceImpl(accountService);
    }

    @Bean
    public OAuth2AuthorizationService oAuth2AuthorizationService(RedisTemplate<String, Object> redisTemplate) {
        return new RedisOAuth2AuthorizationService(redisTemplate);
    }

    @Bean
    public OAuth2AuthorizationConsentService oAuth2AuthorizationConsentService() {
        return new RedisOAuth2AuthorizationConsentServiceImpl();
    }

    /**
     * 令牌生成规则实现 </br>
     * client:username:uuid
     * @return OAuth2TokenGenerator
     */
    @Bean
    public OAuth2TokenGenerator oAuth2TokenGenerator() {
        DefaultOauth2AccessTokenGenerator accessTokenGenerator = new DefaultOauth2AccessTokenGenerator();
        // 注入Token 增加关联用户信息
        accessTokenGenerator.setAccessTokenCustomizer(new DefaultOauth2TokenCustomizer());
        return new DelegatingOAuth2TokenGenerator(accessTokenGenerator, new OAuth2RefreshTokenGenerator());
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http, OAuth2TokenGenerator oAuth2TokenGenerator,
                                                                      MessageSource securityMessageSource, OAuth2AuthorizationService oAuth2AuthorizationService) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();
        http
            //个性化认证授权端点
            .apply(authorizationServerConfigurer.tokenEndpoint((tokenEndpoint) -> {
            // 注入自定义的授权认证Converter
                    tokenEndpoint.accessTokenRequestConverter(accessTokenRequestConverter())
                    // 登录成功处理器
                    .accessTokenResponseHandler(new DefaultAuthenticationSuccessHandler())
                    // 登录失败处理器
                    .errorResponseHandler(new DefaultAuthenticationFailureHandler());
            })
            // 个性化客户端认证
            .clientAuthentication(oAuth2ClientAuthenticationConfigurer ->
            // 处理客户端认证异常
            oAuth2ClientAuthenticationConfigurer.errorResponseHandler(new DefaultAuthenticationFailureHandler()))
            //授权码端点个性化confirm页面
            .authorizationEndpoint(authorizationEndpoint -> authorizationEndpoint.consentPage(SecurityConstants.CUSTOM_CONSENT_PAGE_URI)));

        RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();
        DefaultSecurityFilterChain securityFilterChain = http.requestMatcher(endpointsMatcher)
                .authorizeRequests(authorizeRequests -> authorizeRequests.anyRequest().authenticated())
                .apply(authorizationServerConfigurer.authorizationService(oAuth2AuthorizationService))
                .authorizationServerSettings(AuthorizationServerSettings.builder().issuer(SecurityConstants.PROJECT_LICENSE).build())
                // 授权码登录的登录页个性化
                .and().apply(new FormIdentityLoginConfigurer()).and()
                .httpBasic().and().build();


        // 注入自定义授权模式实现
        addOauth2GrantAuthenticationProvider(http, securityMessageSource);
        return securityFilterChain;
    }

    private void addOauth2GrantAuthenticationProvider(HttpSecurity http, MessageSource securityMessageSource) {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        OAuth2AuthorizationService authorizationService = http.getSharedObject(OAuth2AuthorizationService.class);

        Oauth2ResourceOwnerPasswordAuthenticationProvider resourceOwnerPasswordAuthenticationProvider = new Oauth2ResourceOwnerPasswordAuthenticationProvider(
                authenticationManager, authorizationService, oAuth2TokenGenerator(), securityMessageSource);
        Oauth2ResourceOwnerEmailAuthenticationProvider resourceOwnerEmailAuthenticationProvider = new Oauth2ResourceOwnerEmailAuthenticationProvider(authenticationManager, authorizationService,
                oAuth2TokenGenerator(), securityMessageSource);

        // 处理 UsernamePasswordAuthenticationToken
        http.authenticationProvider(new DefaultDaoAuthenticationProvider(securityMessageSource));
        // 处理 OAuth2ResourceOwnerPasswordAuthenticationToken
        http.authenticationProvider(resourceOwnerPasswordAuthenticationProvider);
        // 处理 Oauth2ResourceOwnerEmailAuthenticationProvider
        http.authenticationProvider(resourceOwnerEmailAuthenticationProvider);
    }

    private AuthenticationConverter accessTokenRequestConverter() {
        return new DelegatingAuthenticationConverter(Arrays.asList(
                new Oauth2ResourceOwnerPasswordAuthenticationConverter(),
                new Oauth2ResourceOwnerEmailAuthenticationConverter(),
                new OAuth2RefreshTokenAuthenticationConverter(),
                new OAuth2ClientCredentialsAuthenticationConverter(),
                new OAuth2AuthorizationCodeAuthenticationConverter(),
                new OAuth2AuthorizationCodeRequestAuthenticationConverter()
        ));
    }



}
