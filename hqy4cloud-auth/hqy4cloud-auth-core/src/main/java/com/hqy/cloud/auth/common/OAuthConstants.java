package com.hqy.cloud.auth.common;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/20
 */
public interface OAuthConstants {


    /**
     * 配置业务鉴权的白名单uri列表key
     */
    String BUSINESS_WHITE_URIS_KEY = "hqy4cloud.auth.white.uris";

    /**
     * 静态的业务鉴权的白名单uri
     */
    List<String> DEFAULT_BUSINESS_WHITE_URIS = List.of("/message/websocket/**", "/blog/**", "/im/**", "/message.socket");

            ;
}
