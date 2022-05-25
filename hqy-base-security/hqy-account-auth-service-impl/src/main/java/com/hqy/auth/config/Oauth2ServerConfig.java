package com.hqy.auth.config;

import com.hqy.auth.server.CustomUserAuthenticationConverter;
import com.hqy.auth.server.DefaultClientDetailsServiceImpl;
import com.hqy.auth.server.JwtTokenEnhancer;
import com.hqy.util.spring.SpringContextHolder;
import org.springframework.cloud.bootstrap.encrypt.KeyProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import javax.annotation.Resource;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * oauth2资源认证器配置 即认证服务器配置
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/11 15:25
 */
@Configuration
@EnableAuthorizationServer
public class Oauth2ServerConfig extends AuthorizationServerConfigurerAdapter {


    @Resource
    private JwtTokenEnhancer jwtTokenEnhancer;

    /**
     * 认证管理器 密码管理方式
     */
    @Resource
    private AuthenticationManager authenticationManager;


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

    @Bean
    public KeyProperties keyProperties(){
        return new KeyProperties();
    }

    @Bean
    public KeyPair keyPair(KeyProperties keyProperties) {
        KeyProperties.KeyStore keyStore = keyProperties.getKeyStore();
        //证书位置, 证书秘钥
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(keyStore.getLocation(), keyStore.getPassword().toCharArray());
        //证书别名 证书密码
        return keyStoreKeyFactory.getKeyPair(keyStore.getAlias(), keyStore.getPassword().toCharArray());
    }


    /**
     * 客户端信息配置
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        DefaultClientDetailsServiceImpl clientDetailsService = SpringContextHolder.getBean(DefaultClientDetailsServiceImpl.class);
        clients.withClientDetails(clientDetailsService);
    }

    /**
     * /oauth/authorize         授权访问端点
     * /auth/token              令牌端点
     * /auth/confirm-access     用户确认授权提交端点
     * /auth/error              授权服务信息错误端点
     * /auth/check_token        用户资源服务访问的令牌解析端点
     * /auth/token_key          提供公有密钥的端点
     *
     * 授权服务器端点配置
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        //jwt令牌转换器
        JwtAccessTokenConverter jwtAccessTokenConverter = SpringContextHolder.getBean(JwtAccessTokenConverter.class);
        TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> enhancers = new ArrayList<>();
        enhancers.add(jwtTokenEnhancer);
        enhancers.add(jwtAccessTokenConverter);
        enhancerChain.setTokenEnhancers(enhancers);

        // 获取原有默认授权模式(授权码模式、密码模式、客户端模式、简化模式)的授权者
        List<TokenGranter> granterList = new ArrayList<>(Collections.singletonList(endpoints.getTokenGranter()));
        CompositeTokenGranter compositeTokenGranter = new CompositeTokenGranter(granterList);

        endpoints.accessTokenConverter(jwtAccessTokenConverter)
                .authenticationManager(authenticationManager)
                .tokenEnhancer(enhancerChain)
                .tokenGranter(compositeTokenGranter)
                .tokenServices(tokenServices(endpoints, enhancerChain));
    }

    /**
     * 授权服务器的安全配置 令牌访问端点的安全策略
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.allowFormAuthenticationForClients()
                .passwordEncoder(SpringContextHolder.getBean(BCryptPasswordEncoder.class))
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("permitAll()")
                .allowFormAuthenticationForClients();
    }


    private AuthorizationServerTokenServices tokenServices(AuthorizationServerEndpointsConfigurer endpoints, TokenEnhancerChain enhancerChain) {
        DefaultClientDetailsServiceImpl  clientDetailsService =
                SpringContextHolder.getBean(DefaultClientDetailsServiceImpl.class);
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        //令牌存储策略
        tokenServices.setTokenStore(endpoints.getTokenStore());
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setClientDetailsService(clientDetailsService);
        tokenServices.setTokenEnhancer(enhancerChain);
        //令牌有效期2小时
//        tokenServices.setAccessTokenValiditySeconds(7200);
        //refresh token有效期
//        tokenServices.setRefreshTokenValiditySeconds(7200 * 5);
        tokenServices.setAuthenticationManager(authenticationManager);

        return tokenServices;
    }
}
