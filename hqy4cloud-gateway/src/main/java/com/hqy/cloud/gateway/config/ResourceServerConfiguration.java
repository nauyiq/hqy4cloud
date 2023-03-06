package com.hqy.cloud.gateway.config;

import com.hqy.cloud.auth.core.support.CustomReactiveOpaqueTokenIntrospector;
import com.hqy.cloud.auth.server.support.EndpointAuthorizationManager;
import com.hqy.cloud.common.bind.MessageResponse;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.CommonResultCode;
import com.hqy.cloud.gateway.filter.SecurityAuthenticationFilter;
import com.hqy.cloud.gateway.server.auth.AuthorizationManager;
import com.hqy.cloud.gateway.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

/**
 * 网关承担资源服务器
 * @author qiyuan.hong
 * @date 2022-03-14 11:43
 */
@Slf4j
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class ResourceServerConfiguration {

    private final OAuth2AuthorizationService oAuth2AuthorizationService;
    private final MessageSource messageSource;


    @Bean
    public ReactiveOpaqueTokenIntrospector opaqueTokenIntrospector() {
        return new CustomReactiveOpaqueTokenIntrospector(oAuth2AuthorizationService);
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.setAllowCredentials(true);
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityWebFilterChain webFluxFilterChain(ServerHttpSecurity http, AuthorizationManager authorizationManager) {
        http.oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer  ->
                        httpSecurityOAuth2ResourceServerConfigurer.opaqueToken()
                                .introspector(opaqueTokenIntrospector()))
                        .addFilterAt(new SecurityAuthenticationFilter(), SecurityWebFiltersOrder.LAST);

        http.authorizeExchange().pathMatchers(HttpMethod.OPTIONS, "/**").permitAll();
        ArrayList<String> whiteEndpoints = new ArrayList<>(EndpointAuthorizationManager.ENDPOINTS);
        http.authorizeExchange().pathMatchers(whiteEndpoints.toArray(new String[0])).permitAll();

        http.authorizeExchange()
                //鉴权管理器配置
                .anyExchange().access(authorizationManager)
                .and().exceptionHandling()
                //处理未授权
                .accessDeniedHandler(accessDeniedHandler())
                //处理未认证
                .authenticationEntryPoint(authenticationEntryPoint())
                .and().cors().and().csrf().disable();
        return http.build();
    }


    @Bean
    ServerAuthenticationFailureHandler authenticationFailureHandler() {
        return (webFilterExchange, exception) -> {
            ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
            return Mono.defer(() -> {
                MessageResponse code = CommonResultCode.messageResponse(CommonResultCode.INVALID_ACCESS_USER);
                DataBuffer buffer = ResponseUtil.outputBuffer(code, response, HttpStatus.OK);
                return response.writeWith(Flux.just(buffer));
            });
        };
    }

    /**
     * 自定义未授权响应
     */
    @Bean
    ServerAccessDeniedHandler accessDeniedHandler() {
        return (exchange, denied) -> Mono.defer(()-> {
            ServerHttpResponse response = exchange.getResponse();
            R<String> result = R.setResult(false, CommonResultCode.LIMITED_AUTHORITY, denied.getMessage());
            DataBuffer buffer = ResponseUtil.outputBuffer(result, response, HttpStatus.FORBIDDEN);
            return response.writeWith(Flux.just(buffer));
        });
    }


    @Bean
    public ServerAuthenticationEntryPoint authenticationEntryPoint() {
        return (exchange, e) -> {
            R<String> result = R.setResult(false, CommonResultCode.INVALID_ACCESS_TOKEN, e.getMessage());
            if (e instanceof InvalidBearerTokenException
                    || e instanceof InsufficientAuthenticationException) {
                result.setMessage(this.messageSource.getMessage("OAuth2ResourceOwnerBaseAuthenticationProvider.tokenExpired",
                        null, LocaleContextHolder.getLocale()));
            }
            ServerHttpResponse response = exchange.getResponse();
            return Mono.defer(() -> {
                DataBuffer buffer = ResponseUtil.outputBuffer(result, response, HttpStatus.UNAUTHORIZED);
                return response.writeWith(Flux.just(buffer));
            });
        };
    }



}
