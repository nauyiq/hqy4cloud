package com.hqy.cloud.gateway.config;

import com.hqy.cloud.auth.api.AuthPermissionService;
import com.hqy.cloud.auth.common.UserRole;
import com.hqy.cloud.auth.security.core.DefaultReactiveOpaqueTokenIntrospector;
import com.hqy.cloud.auth.security.core.RedisOAuth2AuthorizationService;
import com.hqy.cloud.common.bind.MessageResponse;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.gateway.filter.SecurityAuthenticationFilter;
import com.hqy.cloud.gateway.server.auth.AuthorizationManager;
import com.hqy.cloud.gateway.server.auth.ClientSecretReactiveAuthenticationManager;
import com.hqy.cloud.gateway.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    private final MessageSource securityMessageSource;
    private final AuthPermissionService authPermissionService;

    @Bean
    public OAuth2AuthorizationService oAuth2AuthorizationService(RedisTemplate<String, Object> redisTemplate) {
        return new RedisOAuth2AuthorizationService(redisTemplate);
    }

    @Bean
    public ReactiveOpaqueTokenIntrospector opaqueTokenIntrospector(OAuth2AuthorizationService oAuth2AuthorizationService) {
        return new DefaultReactiveOpaqueTokenIntrospector(oAuth2AuthorizationService);
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.setAllowCredentials(true);
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityWebFilterChain webFluxFilterChain(ServerHttpSecurity http,  ReactiveOpaqueTokenIntrospector opaqueTokenIntrospector) {
        http
            .authorizeExchange(exchange -> exchange
                // 放行所有option请求
                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // 白名单uri
                .pathMatchers(authPermissionService.getWhiteUris().toArray(new String[0])).permitAll()
                // 默认所有请求都需要登录
                .pathMatchers("/**").hasAnyAuthority(UserRole.CUSTOMER.name(), UserRole.ADMIN.name())
            )
                // 通用的请求授权走AuthorizationManager
//            .authorizeExchange(spec -> spec.anyExchange().access(authorizationManager))
            .oauth2ResourceServer(oauth2ResourceServerCustomizer -> oauth2ResourceServerCustomizer
                    // token认证失败
                    .authenticationEntryPoint(authenticationEntryPoint())
                    // 开放式token
                    .opaqueToken(c -> c.introspector(opaqueTokenIntrospector)))
            .addFilterAt(new SecurityAuthenticationFilter(), SecurityWebFiltersOrder.LAST)
            .exceptionHandling(handler -> handler
                    // 拒绝策略
                    .accessDeniedHandler(accessDeniedHandler())
                    .authenticationEntryPoint(authenticationEntryPoint()))
                // basic 认证
            .httpBasic(b -> b.authenticationManager(new ClientSecretReactiveAuthenticationManager()))
            .cors(ServerHttpSecurity.CorsSpec::disable)
            .csrf(ServerHttpSecurity.CsrfSpec::disable);
        return http.build();
    }


    @Bean
    ServerAuthenticationFailureHandler authenticationFailureHandler() {
        return (webFilterExchange, exception) -> {
            ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
            return Mono.defer(() -> {
                MessageResponse code = ResultCode.messageResponse(ResultCode.INVALID_ACCESS_USER);
                DataBuffer buffer = ResponseUtil.outputBuffer(code, response, HttpStatus.UNAUTHORIZED);
                return response.writeWith(Flux.just(buffer));
            });

        };
    }

    @Bean
    ServerAccessDeniedHandler accessDeniedHandler() {
        return (exchange, denied) -> Mono.defer(()-> {
            ServerHttpResponse response = exchange.getResponse();
            R<String> result = R.setResult(false, ResultCode.LIMITED_AUTHORITY, denied.getMessage());
            DataBuffer buffer = ResponseUtil.outputBuffer(result, response, HttpStatus.FORBIDDEN);
            return response.writeWith(Flux.just(buffer));
        });
    }


    @Bean
    public ServerAuthenticationEntryPoint authenticationEntryPoint() {
        return (exchange, e) -> {
            R<String> result = R.setResult(false, ResultCode.INVALID_ACCESS_TOKEN, e.getMessage());
            /*if (e instanceof InvalidBearerTokenException
                    || e instanceof InsufficientAuthenticationException) {
                result.setMessage(this.securityMessageSource.getMessage("OAuth2ResourceOwnerBaseAuthenticationProvider.tokenExpired",
                        null, LocaleContextHolder.getLocale()));
            }*/
            ServerHttpResponse response = exchange.getResponse();
            return Mono.defer(() -> {
                DataBuffer buffer = ResponseUtil.outputBuffer(result, response, HttpStatus.UNAUTHORIZED);
                return response.writeWith(Flux.just(buffer));
            });
        };
    }



}
