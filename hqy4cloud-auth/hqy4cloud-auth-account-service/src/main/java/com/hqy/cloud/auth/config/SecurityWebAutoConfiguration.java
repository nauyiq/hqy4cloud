package com.hqy.cloud.auth.config;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.hqy.cloud.auth.core.component.EndpointAuthorizationManager;
import com.hqy.cloud.auth.support.core.DefaultDaoAuthenticationProvider;
import com.hqy.cloud.auth.support.core.FormIdentityLoginConfigurer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 安全拦截机制
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/11 10:47
 */
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityWebAutoConfiguration {

    /**
     * spring security 默认的安全策略
     * @param http security注入点
     * @return SecurityFilterChain
     * @throws Exception e
     */
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, MessageSource securityMessageSource) throws Exception {
        // 开放自定义的部分端点
        http.authorizeRequests(authorizeRequests -> authorizeRequests
                // 开放白名单端点
                .antMatchers(EndpointAuthorizationManager.ENDPOINTS.toArray(new String[0])).permitAll()
                // 开放options请求
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // 避免iframe同源无法登录
                .anyRequest().authenticated()).headers().frameOptions().sameOrigin().
                // 表单登录个性化
                and().apply(new FormIdentityLoginConfigurer());
                // 处理 UsernamePasswordAuthenticationToken
        http.authenticationProvider(new DefaultDaoAuthenticationProvider(securityMessageSource));
        return http.build();
    }

    @Bean
    public ServletRegistrationBean statViewServlet(){
        //druid监控页面的url
        ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet(),"/druid/*");
        Map<String,String> initParams = new HashMap<>();

        initParams.put("loginUsername","druid");   //登陆用户名
        initParams.put("loginPassword","123456");  //密码
        initParams.put("allow","");                //允许哪些ip
        initParams.put("deny","");                 //拒绝ip
        bean.setInitParameters(initParams);
        return bean;
    }

    //2.配置一个web监控的filter,监控sql
    @Bean
    public FilterRegistrationBean webStatFilter(){
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(new WebStatFilter());

        Map<String,String> initParams = new HashMap<>();
        initParams.put("exclusions","*.js,*.css,*.html,/druid/*");
        bean.setInitParameters(initParams);
        bean.setUrlPatterns(Arrays.asList("/*"));
        return bean;
    }


}
