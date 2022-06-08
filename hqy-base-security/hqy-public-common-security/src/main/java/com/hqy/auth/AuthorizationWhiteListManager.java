package com.hqy.auth;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 鉴权白名单管理器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/25 14:26
 */
public class AuthorizationWhiteListManager {

    private AuthorizationWhiteListManager() {}

    private static volatile AuthorizationWhiteListManager instance = null;

    private static final Set<String> ENDPOINTS = new CopyOnWriteArraySet<>();

    public static AuthorizationWhiteListManager getInstance() {
        if (instance == null) {
            synchronized (AuthorizationWhiteListManager.class) {
                if (instance == null) {
                    instance = new AuthorizationWhiteListManager();
                }
            }
        }
        return instance;
    }

    static {
        ENDPOINTS.add("/message/websocket/**");

        ENDPOINTS.addAll(Arrays.asList(
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
    }


    public Set<String> endpoints() {
        return ENDPOINTS;
    }


    public void addEndpoint(String value) {
        ENDPOINTS.add(value);
    }






}
