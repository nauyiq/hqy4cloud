package com.hqy.cloud.gateway.config;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.IoUtil;
import com.hqy.cloud.auth.core.support.CustomReactiveOpaqueTokenIntrospector;
import com.hqy.cloud.auth.limit.support.BiBlockedIpRedisService;
import com.hqy.cloud.auth.limit.support.ManualBlockedIpService;
import com.hqy.cloud.auth.limit.support.ManualWhiteIpRedisService;
import com.hqy.cloud.auth.server.Oauth2Access;
import com.hqy.cloud.auth.server.RolesAuthoritiesChecker;
import com.hqy.cloud.auth.server.UploadFileSecurityChecker;
import com.hqy.cloud.auth.server.support.DefaultUploadFileSecurityChecker;
import com.hqy.cloud.auth.server.support.EndpointAuthorizationManager;
import com.hqy.cloud.auth.server.support.NacosOauth2Access;
import com.hqy.cloud.auth.server.support.ResourceInRoleCacheServer;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.bind.MessageResponse;
import com.hqy.cloud.common.result.CommonResultCode;
import com.hqy.cloud.gateway.server.auth.AuthorizationManager;
import com.hqy.cloud.gateway.server.auth.DefaultJwtGrantedAuthoritiesConverter;
import com.hqy.cloud.gateway.server.auth.GatewayReactOauth2AuthoritiesChecker;
import com.hqy.cloud.gateway.util.ResponseUtil;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.foundation.limit.service.BlockedIpService;
import com.hqy.foundation.limit.service.ManualWhiteIpService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;

/**
 * 网关承担资源服务器
 * @author qiyuan.hong
 * @date 2022-03-14 11:43
 */
@Slf4j
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class ResourceServerConfiguration {

    private final OAuth2AuthorizationService oAuth2AuthorizationService;

    @Bean
    public ResourceInRoleCacheServer resourceInRoleCacheServer(RedissonClient redissonClient) {
        return new ResourceInRoleCacheServer(redissonClient);
    }

    @Bean
    public Oauth2Access oauth2Access(ManualWhiteIpService manualWhiteIpService) {
        return new NacosOauth2Access(manualWhiteIpService);
    }

    @Bean
    public GatewayReactOauth2AuthoritiesChecker gatewayReactOauth2AuthoritiesChecker(ResourceInRoleCacheServer resourceInRoleCacheServer) {
        return new GatewayReactOauth2AuthoritiesChecker(resourceInRoleCacheServer);
    }

    @Bean
    public AuthorizationManager authorizationManager(RolesAuthoritiesChecker gatewayReactOauth2AuthoritiesChecker, Oauth2Access oauth2Access) {
        return new AuthorizationManager(gatewayReactOauth2AuthoritiesChecker, oauth2Access);
    }

    @Bean
    public ManualWhiteIpService manualWhiteIpService(RedissonClient redisson) {
        return new ManualWhiteIpRedisService(redisson);
    }

    @Bean
    public BlockedIpService biBlockedIpService() {
        return new BiBlockedIpRedisService(true);
    }

    @Bean
    public BlockedIpService manualBlockedIpService() {
        return new ManualBlockedIpService(true);
    }

    @Bean
    public UploadFileSecurityChecker uploadFileSecurityChecker() {
        return new DefaultUploadFileSecurityChecker();
    }

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
                                .introspector(opaqueTokenIntrospector()));

//        http.authorizeExchange().pathMatchers(HttpMethod.OPTIONS, "/**").permitAll();

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
            MessageResponse code =  CommonResultCode.messageResponse(CommonResultCode.LIMITED_AUTHORITY);
            DataBuffer buffer = ResponseUtil.outputBuffer(code, response, HttpStatus.FORBIDDEN);
            return response.writeWith(Flux.just(buffer));
        });
    }


    /**
     * token过期或者无效
     */
    @Bean
    public ServerAuthenticationEntryPoint authenticationEntryPoint() {
        return (exchange, e) -> {
            log.warn("@@@ RestAuthenticationEntryPoint访问受限, e:{}", e.getMessage());
            ServerHttpResponse response = exchange.getResponse();
            return Mono.defer(() -> {
                MessageResponse code = CommonResultCode.messageResponse(CommonResultCode.INVALID_ACCESS_TOKEN);
                DataBuffer buffer = ResponseUtil.outputBuffer(code, response, HttpStatus.UNAUTHORIZED);
                return response.writeWith(Flux.just(buffer));
            });
        };
    }


//    @Bean
    @SneakyThrows
    public RSAPublicKey rsaPublicKey() {
        String fileName = "public.key";
        try(InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(fileName)) {
            AssertUtil.notNull(inputStream, "Get resources public key failure, filename = " + fileName);
            String data = IoUtil.read(inputStream).toString();
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec((Base64.decode(data)));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPublicKey)keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }


//    @Bean
    public Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter() {
//        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        DefaultJwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new DefaultJwtGrantedAuthoritiesConverter();
//        jwtGrantedAuthoritiesConverter.setAuthorityPrefix(StringConstants.Auth.AUTHORITY_PREFIX);
        //取消权限的前缀，默认会加上SCOPE_
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix(StringConstants.EMPTY);
        //从哪个字段中获取权限
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
//        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName(StringConstants.Auth.JWT_AUTHORITIES_KEY);

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

}
