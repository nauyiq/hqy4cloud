package com.hqy.auth.config;

import com.hqy.auth.access.server.AuthorizationWhiteListManager;
import com.hqy.auth.server.AuthUserDetailServiceImpl;
import com.hqy.util.spring.SpringContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.annotation.Resource;

/**
 * 安全拦截机制
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/11 10:47
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 验证成功处理器
     */
    @Resource
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    /**
     * 验证失败处理器
     */
    @Resource
    private AuthenticationFailureHandler authenticationFailureHandler;


    @Resource
    private UserDetailsService userDetailsService;


    /**
     * 自定义异常处理端口 默认空
     */
    @Autowired(required = false)
    private AuthenticationEntryPoint authenticationEntryPoint;


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 全局用户信息
     * @param auth 认证管理
     * @throws Exception 用户认证异常信息
     */
    @Autowired
    public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider()) ;
    }

    /**
     * 用户名密码认证授权提供者
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        provider.setHideUserNotFoundExceptions(false);
        return provider;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(AuthorizationWhiteListManager.getInstance().endpoints().toArray(new String[0]));
        super.configure(web);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();

        http.authorizeRequests()
                .anyRequest().authenticated();
        http.formLogin().loginProcessingUrl(AuthorizationWhiteListManager.SecurityContext.LOGIN_PROCESSING_URL)
                        .successHandler(authenticationSuccessHandler).failureHandler(authenticationFailureHandler);

        // 基于密码 等模式可以无session,不支持授权码模式
        if (authenticationEntryPoint == null) {
            http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
            http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        } else {
            // 授权码模式单独处理，需要session的支持，此模式可以支持所有oauth2的认证
            http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
        }


        http.headers().frameOptions().disable();
        http.headers().cacheControl();


//        http.csrf().disable().cors().disable()
//                .authorizeRequests().requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll().and()
//                .authorizeRequests().antMatchers("/oauth/**","/auth/**").permitAll()
//                .and().formLogin().permitAll();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        DaoAuthenticationProvider daoAuthenticationProvider = SpringContextHolder.getBean(DaoAuthenticationProvider.class);
        auth.authenticationProvider(daoAuthenticationProvider);
    }
}
