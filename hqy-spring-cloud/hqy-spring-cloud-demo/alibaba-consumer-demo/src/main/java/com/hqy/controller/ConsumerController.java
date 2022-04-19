package com.hqy.controller;

import com.hqy.base.common.bind.DataResponse;
import com.hqy.util.spring.SpringContextHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author qy
 * @create 2021/8/4 23:10
 */
@RestController
public class ConsumerController {

    @Value("${service-url.nacos-user-service}")
    private String provideDemoUrl;


    @GetMapping("/consumer/demo/{id}")
    public DataResponse getProviderInfo(@PathVariable("id") Long id) {
        RestTemplate restTemplate = SpringContextHolder.getBean(RestTemplate.class);
        return restTemplate.getForObject(provideDemoUrl + "/payment/" + id, DataResponse.class);
    }



}
