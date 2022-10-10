package com.hqy.access.auth.support;

import org.springframework.util.AntPathMatcher;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 鉴权白名单管理器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/25 14:26
 */
public class EndpointAuthorizationManager {

    private EndpointAuthorizationManager() {}

    private static volatile EndpointAuthorizationManager instance = null;

    public static final Set<String> ENDPOINTS = new CopyOnWriteArraySet<>();
    private static final Set<String> ADMIN_ENDPOINTS = new CopyOnWriteArraySet<>();
    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    public static EndpointAuthorizationManager getInstance() {
        if (instance == null) {
            synchronized (EndpointAuthorizationManager.class) {
                if (instance == null) {
                    instance = new EndpointAuthorizationManager();
                }
            }
        }
        return instance;
    }

    static {
        //静态白名单endpoint.
        ENDPOINTS.addAll(Arrays.asList(
                "favicon.io",
                //Oauth2 Endpoint
                "/oauth/**", "/auth/**",
                //端点监控
                "/**/actuator/**", "/**/actuator/**/**", "/**/doc.html", "/doc.html",
                //swagger
                "/v2/api-docs/**", "/swagger-ui.html", "/swagger-resources/**", "/webjars/**",
                "/**/v2/api-docs/**", "/**/swagger-ui.html", "/**/swagger-resources/**", "/**/webjars/**",
                //熔断监控
                "/**/turbine.stream", "/**/turbine.stream**/**", "/**/hystrix", "/**/hystrix.stream", "/**/hystrix/**", "/**/hystrix/**/**", "/**/proxy.stream/**"
        ));

        //必须进行身份验证的endpoint.
        ADMIN_ENDPOINTS.addAll(Arrays.asList(
                "/admin/**", "/**/admin/**"
        ));
    }

    public void addEndpoint(String value) {
        ENDPOINTS.add(value);
    }



    public boolean isStaticWhiteEndpoint(String endpoint) {
        return ENDPOINTS.stream().anyMatch(r -> ANT_PATH_MATCHER.match(r, endpoint));
    }

    public boolean isMatch(List<String> endpoints, String accessUri) {
        return endpoints.stream().anyMatch(r -> ANT_PATH_MATCHER.match(r, accessUri));
    }

    public boolean isAdminRequest(String accessUri) {
        return ADMIN_ENDPOINTS.stream().anyMatch(r -> ANT_PATH_MATCHER.match(r, accessUri));
    }






}
