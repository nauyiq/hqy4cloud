package com.hqy.security.config;

import com.hqy.security.core.client.SecurityClientDetailsServiceImpl;
import com.hqy.security.server.PasswordEnhanceTokenGranter;
import com.hqy.security.server.RedisAuthorizationCodeServer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.ArrayList;
import java.util.List;

/**
 * oauth2资源认证器配置 即认证服务器配置
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/11 15:25
 */
@Configuration
@RequiredArgsConstructor
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    private final AuthenticationManager authenticationManager;

    private final SecurityClientDetailsServiceImpl clientDetailsService;

    private final JwtTokenStore tokenStore;

    private final TokenEnhancer tokenEnhancer;

    private final JwtAccessTokenConverter jwtAccessTokenConverter;

    private final RedisAuthorizationCodeServer redisAuthorizationCodeServer;


    /**
     * 客户端信息配置
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientDetailsService);
    }

    /**
     * 配置身份认证器，配置认证方式，TokenStore，TokenGranter，OAuth2RequestFactory
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

        // Token增强
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> tokenEnhancers = new ArrayList<>();
        tokenEnhancers.add(tokenEnhancer);
        tokenEnhancers.add(jwtAccessTokenConverter);
        tokenEnhancerChain.setTokenEnhancers(tokenEnhancers);

        //token存储模式设定 默认为InMemoryTokenStore模式存储到内存中
        endpoints.tokenStore(tokenStore);

        //处理授权码
        endpoints.authorizationCodeServices(redisAuthorizationCodeServer);

        //处理oauth 模式
        ClientDetailsService clientDetails = endpoints.getClientDetailsService();
        AuthorizationServerTokenServices tokenServices = endpoints.getTokenServices();
        AuthorizationCodeServices authorizationCodeServices = endpoints.getAuthorizationCodeServices();
        OAuth2RequestFactory requestFactory = endpoints.getOAuth2RequestFactory();

        //tokenGranters添加oauth模式 ，可以让/oauth/token支持自定义模式，继承AbstractTokenGranter 扩展
        List<TokenGranter> tokenGranters = new ArrayList<>();
        //客户端模式   GRANT_TYPE = "client_credentials";
        tokenGranters.add(new ClientCredentialsTokenGranter(tokenServices, clientDetails, requestFactory));
        //密码模式	  GRANT_TYPE = "password";
        tokenGranters.add(new PasswordEnhanceTokenGranter(authenticationManager, tokenServices,clientDetails, requestFactory));
        //授权码模式   GRANT_TYPE = "authorization_code";
        tokenGranters.add(new AuthorizationCodeTokenGranter(tokenServices, authorizationCodeServices, clientDetails,requestFactory));
        //刷新模式	  GRANT_TYPE = "refresh_token";
        tokenGranters.add(new RefreshTokenGranter(tokenServices, clientDetails, requestFactory));
        //简易模式	  GRANT_TYPE = "implicit";
        tokenGranters.add(new ImplicitTokenGranter(tokenServices, clientDetails, requestFactory));

        endpoints
                .authenticationManager(authenticationManager)
                .accessTokenConverter(jwtAccessTokenConverter)
                .tokenEnhancer(tokenEnhancerChain)
                .tokenGranter(new CompositeTokenGranter(tokenGranters))
                .tokenServices(tokenServices(endpoints));

    }

    /**
     * 授权服务器的安全配置 令牌访问端点的安全策略
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()")
                .allowFormAuthenticationForClients();
    }



    private AuthorizationServerTokenServices tokenServices(AuthorizationServerEndpointsConfigurer endpoints) {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> tokenEnhancers = new ArrayList<>();
        tokenEnhancers.add(tokenEnhancer);
        tokenEnhancers.add(jwtAccessTokenConverter);
        tokenEnhancerChain.setTokenEnhancers(tokenEnhancers);

        DefaultTokenServices tokenServices = new DefaultTokenServices();
        //令牌存储策略
        tokenServices.setTokenStore(endpoints.getTokenStore());
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setClientDetailsService(clientDetailsService);
        tokenServices.setTokenEnhancer(tokenEnhancerChain);

        /* refresh_token有两种使用方式：重复使用(true)、非重复使用(false)，默认为true
           1 重复使用：access_token过期刷新时， refresh_token过期时间未改变，仍以初次生成的时间为准
           2 非重复使用：access_token过期刷新时， refresh_token过期时间延续，在refresh_token有效期内刷新便永不失效达到无需再次登录的目的
         */
        tokenServices.setReuseRefreshToken(true);

        return tokenServices;
    }



}
