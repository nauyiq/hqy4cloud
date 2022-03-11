package com.hqy.auth.config;

import com.hqy.auth.service.impl.UserDetailServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import javax.sql.DataSource;

/**
 * oauth2资源认证器配置 即认证服务器配置
 * 通过构造方法注入属性
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/11 15:25
 */
@Configuration
@AllArgsConstructor
@EnableAuthorizationServer
public class Oauth2ServerConfig extends AuthorizationServerConfigurerAdapter {

    /**
     * db
     */
    private final DataSource dataSource;

    /**
     * spring security 加载用户核心数据
     */
    private final UserDetailServiceImpl userDetailsService;

    /**
     * 认证管理器
     */
    private final AuthenticationManager authenticationManager;


    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

    }
}
