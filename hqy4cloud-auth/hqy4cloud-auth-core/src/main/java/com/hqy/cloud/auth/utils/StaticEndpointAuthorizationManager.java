package com.hqy.cloud.auth.utils;

import org.springframework.util.AntPathMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 静态端点鉴权管理者.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/25
 */
public class StaticEndpointAuthorizationManager {

    private StaticEndpointAuthorizationManager() {}

    private static final StaticEndpointAuthorizationManager INSTANCE = new StaticEndpointAuthorizationManager();

    public static StaticEndpointAuthorizationManager getInstance() {
        return INSTANCE;
    }

    /**
     * 静态端点白名单url pattern 列表
     */
    private static final List<String> WHITE_ENDPOINTS_PATTERNS = List.of(
            "/code",
            "/token",
            "/favicon.io","/favicon.ico",
            //Oauth2 Endpoint
            "/oauth/**", "/auth/**", "/oauth2/**", "/token/**",
            "/basic/**", "/actuator/**", "/druid/**"
            //swagger
//                "/v2/api-docs/**", "/swagger-ui.html", "/swagger-resources/**", "/webjars/**",
//                "/**/v2/api-docs/**", "/**/swagger-ui.html", "/**/swagger-resources/**", "/**/webjars/**",
            //熔断监控
//                "/**/turbine.stream", "/**/turbine.stream**/**", "/**/hystrix", "/**/hystrix.stream", "/**/hystrix/**", "/**/hystrix/**/**", "/**/proxy.stream/**"
    );

    /**
     * aop校验类型请求url pattern列表
     */
    private static final List<String> USING_AOP_CHECK_ENDPOINTS = List.of("/admin/**");

    /**
     * 进行basic类型校验的url请求.
     */
    private static final List<String> USING_BASIC_CHECK_ENDPOINTS = List.of("/basic/**", "/actuator/**", "/druid/**");

    /**
     * actuator请求
     */
    private static final List<String> ACTUATOR_URI_PATTERN = List.of(
            "/actuator/**", "**/actuator/**"
    );

    /**
     * 请求匹配器
     */
    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();


    /**
     * 判断当前请求是否需要进权限校验.
     * @param uri uri请求
     * @return    是否需要进行权限校验
     */
    public boolean isNeedPermissionRequest(String uri) {



    }


    public boolean isStaticWhiteEndpoint(String endpoint) {
        return WHITE_ENDPOINTS_PATTERNS.stream().anyMatch(r -> ANT_PATH_MATCHER.match(r, endpoint));
    }


    public boolean isActuatorRequest(String accessUri) {
        return ACTUATOR_URI_PATTERN.stream().anyMatch(r -> ANT_PATH_MATCHER.match(r, accessUri));
    }

    public boolean isMatch(List<String> endpoints, String accessUri) {
        return endpoints.stream().anyMatch(r -> ANT_PATH_MATCHER.match(r, accessUri));
    }



    public AntPathMatcher getAntPathMatcher() {
        return ANT_PATH_MATCHER;
    }



}
