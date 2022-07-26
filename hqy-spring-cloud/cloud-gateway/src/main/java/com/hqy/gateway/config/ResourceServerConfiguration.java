package com.hqy.gateway.config;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.IoUtil;
import com.hqy.access.auth.AuthorizationWhiteListManager;
import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.base.common.bind.MessageResponse;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.gateway.server.AuthorizationManager;
import com.hqy.gateway.util.ResponseUtil;
import com.hqy.util.AssertUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Set;

/**
 * 网关承担 资源服务器
 * @author qiyuan.hong
 * @date 2022-03-14 11:43
 */
@Slf4j
@Configuration
@EnableWebFluxSecurity
public class ResourceServerConfiguration {

    @Resource
    private AuthorizationManager authorizationManager;

    @Bean
    public SecurityWebFilterChain webFluxFilterChain(ServerHttpSecurity http) {
        //jwt增强
         http.oauth2ResourceServer().jwt().jwtAuthenticationConverter(jwtAuthenticationConverter())
                //本地公钥
                .publicKey(rsaPublicKey());

        //自定义处理JWT请求头过期或签名错误的结果
        http.oauth2ResourceServer().authenticationEntryPoint(authenticationEntryPoint());

        //获取项目中的uri白名单
        Set<String> whiteUri = AuthorizationWhiteListManager.getInstance().endpoints();
        if (CollectionUtils.isNotEmpty(whiteUri)) {
            //白名单配置
            http.authorizeExchange().pathMatchers(whiteUri.toArray(new String[0])).permitAll();
        }

        http.authorizeExchange()
                //鉴权管理器配置
                .anyExchange().access(authorizationManager)
                .and().exceptionHandling()
                //处理未授权
                .accessDeniedHandler(accessDeniedHandler())
                //处理未认证
                .authenticationEntryPoint(authenticationEntryPoint())
                .and().csrf().disable();
        return http.build();
    }


    /**
     * 自定义未授权响应
     */
    @Bean
    ServerAccessDeniedHandler accessDeniedHandler() {
        return (exchange, denied) -> Mono.defer(()-> {
            ServerHttpResponse response = exchange.getResponse();
            MessageResponse code =  CommonResultCode.messageResponse(CommonResultCode.LIMITED_AUTHORITY);
            DataBuffer buffer = ResponseUtil.outputBuffer(code, response, HttpStatus.UNAUTHORIZED);
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


    /**
     * @SneakyThrows可以用来偷偷抛出已检查的异常而不在方法的throws子句中实际声明这一点
     * 获取本地JWT公钥
     */
    @Bean
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


    /**
     * ServerHttpSecurity没有将jwt中authorities的负载部分当做Authentication
     * 需要把jwt的Claim中的authorities加入
     * 重新定义权限管理器，默认转换器JwtGrantedAuthoritiesConverter
     */
    @Bean
    public Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix(StringConstants.Auth.AUTHORITY_PREFIX);
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName(StringConstants.Auth.JWT_AUTHORITIES_KEY);

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

}
