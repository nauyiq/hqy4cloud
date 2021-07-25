package com.hqy.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 通过bean注入路由
 * @author qy
 * @create 2021/7/25 21:19
 */
@Configuration
public class RouteLocatorConfig {

    @Bean
    public RouteLocator providerRouteLocator(RouteLocatorBuilder locatorBuilder) {
        RouteLocatorBuilder.Builder builder = locatorBuilder.routes();
        builder.route("payment_circuitBreaker_routh", r -> r.path("/payment/circuitBreaker/**").uri("http://localhost:8001")).build();
        return builder.build();
    }

}
