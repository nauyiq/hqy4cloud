package com.hqy.cloud.gateway;

import org.springframework.core.Ordered;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/5 14:19
 */
public interface Constants {

    int WRAPPER_REQUEST_FILTER_ORDER = Ordered.HIGHEST_PRECEDENCE;

    int TOKEN_AUTH_FILTER_ORDER = 0;

    int GLOBAL_HTTP_THROTTLE_FILER_ORDER = 1;

    int LOAD_BALANCER_FILTER_ORDER = 10149;

    int CORS_FILTER_ORDER = Ordered.LOWEST_PRECEDENCE;


}
