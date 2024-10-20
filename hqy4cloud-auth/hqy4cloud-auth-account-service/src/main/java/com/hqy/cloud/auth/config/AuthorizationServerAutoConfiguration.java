package com.hqy.cloud.auth.config;

import com.hqy.cloud.auth.common.SecurityConstants;
import com.hqy.cloud.auth.security.core.RedisOAuth2AuthorizationService;
import com.hqy.cloud.auth.support.core.DefaultDaoAuthenticationProvider;
import com.hqy.cloud.auth.support.core.DefaultOauth2TokenCustomizer;
import com.hqy.cloud.auth.support.core.FormIdentityLoginConfigurer;
import com.hqy.cloud.auth.support.core.RedisOAuth2AuthorizationConsentService;
import com.hqy.cloud.auth.support.core.email.Oauth2ResourceOwnerEmailAuthenticationConverter;
import com.hqy.cloud.auth.support.core.email.Oauth2ResourceOwnerEmailAuthenticationProvider;
import com.hqy.cloud.auth.support.core.password.Oauth2ResourceOwnerPasswordAuthenticationConverter;
import com.hqy.cloud.auth.support.core.password.Oauth2ResourceOwnerPasswordAuthenticationProvider;
import com.hqy.cloud.auth.support.core.sms.Oauth2ResourceOwnerSmsAuthenticationConverter;
import com.hqy.cloud.auth.support.core.sms.Oauth2ResourceOwnerSmsAuthenticationProvider;
import com.hqy.cloud.auth.support.handler.DefaultAuthenticationFailureHandler;
import com.hqy.cloud.auth.support.handler.DefaultAuthenticationSuccessHandler;
import com.hqy.cloud.auth.support.server.DefaultOauth2AccessTokenGenerator;
import com.hqy.cloud.auth.utils.StaticEndpointAuthorizationManager;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2RefreshTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.web.authentication.*;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationConverter;

import java.util.Arrays;

/**
 * 资源服务器配置.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/24 11:30
 */
@Configuration
@SuppressWarnings({"rawtypes", "unchecked"})
public class AuthorizationServerAutoConfiguration {

    @Bean
    public OAuth2AuthorizationService oAuth2AuthorizationService(RedisTemplate<String, Object> redisTemplate) {
        return new RedisOAuth2AuthorizationService(redisTemplate);
    }

    @Bean
    public OAuth2AuthorizationConsentService oAuth2AuthorizationConsentService() {
        return new RedisOAuth2AuthorizationConsentService();
    }

    @Bean
    public AbstractUserDetailsAuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder, UserDetailsService userDetailsService, MessageSource messageSource) {
        DefaultDaoAuthenticationProvider provider = new DefaultDaoAuthenticationProvider(messageSource);
        provider.setPasswordEncoder(passwordEncoder);
        provider.setHideUserNotFoundExceptions(false);
        return provider;
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
                .with(authorizationServerConfigurer

//                                .authorizationService(oAuth2AuthorizationService)
//                                .authorizationServerSettings(AuthorizationServerSettings.builder().issuer(SecurityConstants.PROJECT_LICENSE).build())
                                .tokenEndpoint((tokenEndpoint) -> {
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
                                .authorizationEndpoint(authorizationEndpoint -> authorizationEndpoint.consentPage(SecurityConstants.CUSTOM_CONSENT_PAGE_URI))
                        , (c) -> new FormIdentityLoginConfigurer())
                .with(authorizationServerConfigurer.authorizationService(oAuth2AuthorizationService)
                        .authorizationServerSettings(AuthorizationServerSettings.builder().build()), Customizer.withDefaults());

                http.authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(StaticEndpointAuthorizationManager.getInstance().getWhiteEndpointsPatterns().toArray(new String[0])).permitAll()
                        .anyRequest().authenticated())
                        .csrf(AbstractHttpConfigurer::disable);



//        authorizationServerConfigurer.setBuilder(http);
        DefaultSecurityFilterChain chain = http.build();
        // 注入自定义授权模式实现
        addOauth2GrantAuthenticationProvider(http, securityMessageSource);

        return chain;
    }

    private void addOauth2GrantAuthenticationProvider(HttpSecurity http, MessageSource securityMessageSource) throws Exception {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        OAuth2AuthorizationService authorizationService = http.getSharedObject(OAuth2AuthorizationService.class);
        // 密码认证方式
        Oauth2ResourceOwnerPasswordAuthenticationProvider resourceOwnerPasswordAuthenticationProvider = new Oauth2ResourceOwnerPasswordAuthenticationProvider(
                authenticationManager, authorizationService, oAuth2TokenGenerator(), securityMessageSource);
        // 邮箱验证码认证方式
        Oauth2ResourceOwnerEmailAuthenticationProvider resourceOwnerEmailAuthenticationProvider = new Oauth2ResourceOwnerEmailAuthenticationProvider(authenticationManager, authorizationService,
                oAuth2TokenGenerator(), securityMessageSource);
        // 手机验证码认证方式
        Oauth2ResourceOwnerSmsAuthenticationProvider resourceOwnerSmsAuthenticationProvider = new Oauth2ResourceOwnerSmsAuthenticationProvider(authenticationManager, authorizationService,
                oAuth2TokenGenerator(), securityMessageSource);

        // 处理 OAuth2ResourceOwnerPasswordAuthenticationToken
        http.authenticationProvider(resourceOwnerPasswordAuthenticationProvider);
        // 处理 Oauth2ResourceOwnerEmailAuthenticationProvider
        http.authenticationProvider(resourceOwnerEmailAuthenticationProvider);
        // 处理 Oauth2ResourceOwnerSmsAuthenticationProvider
        http.authenticationProvider(resourceOwnerSmsAuthenticationProvider);
    }

    private AuthenticationConverter accessTokenRequestConverter() {
        return new DelegatingAuthenticationConverter(Arrays.asList(
                new Oauth2ResourceOwnerPasswordAuthenticationConverter(),
                new Oauth2ResourceOwnerEmailAuthenticationConverter(),
                new Oauth2ResourceOwnerSmsAuthenticationConverter(),
                new OAuth2RefreshTokenAuthenticationConverter(),
                new OAuth2ClientCredentialsAuthenticationConverter(),
                new OAuth2AuthorizationCodeAuthenticationConverter(),
                new OAuth2AuthorizationCodeRequestAuthenticationConverter()
        ));
    }



}
