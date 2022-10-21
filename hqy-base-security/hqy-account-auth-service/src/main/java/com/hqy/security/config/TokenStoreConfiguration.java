package com.hqy.security.config;

import cn.hutool.core.bean.BeanUtil;
import com.hqy.security.core.user.SecurityUser;
import com.hqy.security.dto.UserJwtPayloadDTO;
import com.hqy.util.AssertUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import javax.sql.DataSource;
import java.security.KeyPair;
import java.util.Map;

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
   /* @Bean
    public JdbcTokenStore jdbcTokenStore(DataSource dataSource){
        return new JdbcTokenStore(dataSource) ;
    }*/


    @Bean
    public UserAuthenticationConverter userAuthenticationConverter(UserDetailsService userDetailsService) {
        DefaultUserAuthenticationConverter defaultUserAuthenticationConverter = new DefaultUserAuthenticationConverter();
        defaultUserAuthenticationConverter.setUserDetailsService(userDetailsService);
        return defaultUserAuthenticationConverter;
    }

    @Bean
    public JwtTokenStore tokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
        return new JwtTokenStore(jwtAccessTokenConverter);
    }

    @Bean
    public KeyPair keyPair() {
        KeyStoreKeyFactory factory = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), "hongqy@2021".toCharArray());
        return factory.getKeyPair("jwt", "hongqy@2021".toCharArray());
    }

    /**
     * jwt令牌转换器
     * @return JwtAccessTokenConverter
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(UserAuthenticationConverter userAuthenticationConverter) {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setKeyPair(keyPair());
        //配置自定义的CustomUserAuthenticationConverter
        DefaultAccessTokenConverter accessTokenConverter = (DefaultAccessTokenConverter) converter.getAccessTokenConverter();
        accessTokenConverter.setUserTokenConverter(userAuthenticationConverter);
        return converter;
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return (accessToken, authentication) -> {
            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            AssertUtil.notNull(securityUser, "JwtTokenEnhancer enhance failure. principal convert securityUserDTO is null.");
            ((DefaultOAuth2AccessToken)accessToken).setAdditionalInformation(userConvertToMap(securityUser));
            return accessToken;
        };
    }

    private Map<String, Object> userConvertToMap(SecurityUser securityUser) {
        UserJwtPayloadDTO userJwtPayloadDTO = new UserJwtPayloadDTO(securityUser.getId(), securityUser.getPassword(), securityUser.getEmail(), securityUser.getUsername(), securityUser.getAuthorities());
        return BeanUtil.beanToMap(userJwtPayloadDTO);
    }


}
