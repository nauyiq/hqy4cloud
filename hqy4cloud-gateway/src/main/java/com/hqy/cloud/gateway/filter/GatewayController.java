package com.hqy.cloud.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/3 17:34
 */
@Slf4j
@RestController
@RequestMapping("/gatewayController")
public class GatewayController {


    @GetMapping("info/{id}")
    public Mono<String> info(@PathVariable Integer id) throws InterruptedException {
        return ReactiveSecurityContextHolder.getContext()
                .filter(securityContext -> securityContext != null)
                .map(securityContext -> securityContext.getAuthentication())
                .map(auth -> this.getAuthUserName(auth) + ", Request Argument is " + id);
    }

    // 获取登录用户名称
    protected String getAuthUserName(Authentication auth) {
        if (!auth.isAuthenticated()) {
            return "Not Authentication";
        }
        else {
            Object principal = auth.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            }
            else {
                return String.valueOf(principal);
            }
        }
    }


}
