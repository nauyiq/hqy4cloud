package com.hqy.auth.config;

import com.hqy.auth.server.CustomUserAuthenticationConverter;
import com.hqy.util.spring.SpringContextHolder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import javax.sql.DataSource;
import java.security.KeyPair;

/**
 * jwt token 存储配置类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/27 16:06
 */
@Configuration
public class TokenStoreConfiguration {

    /**
     * 基于数据库存储token
     * 需要创建两张表oauth_access_token oauth_refresh_token
     * @param dataSource
     * @return
     */
    @Bean
//    @ConditionalOnProperty(prefix="security.oauth2.token.store", name="type", havingValue="jdbc")
    public JdbcTokenStore jdbcTokenStore(DataSource dataSource ){
        return new JdbcTokenStore(dataSource) ;
    }


    @Bean
    public TokenStore tokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
        return new JwtTokenStore(jwtAccessTokenConverter);
    }


    /**
     * jwt令牌转换器
     * @param authenticationConverter 用户身份验证转换器
     * @return JwtAccessTokenConverter
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(CustomUserAuthenticationConverter authenticationConverter) {
        KeyPair keyPair = SpringContextHolder.getBean(KeyPair.class);
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setKeyPair(keyPair);

        //配置自定义的CustomUserAuthenticationConverter
        DefaultAccessTokenConverter accessTokenConverter = (DefaultAccessTokenConverter) converter.getAccessTokenConverter();
        accessTokenConverter.setUserTokenConverter(authenticationConverter);
        return converter;
    }


}
