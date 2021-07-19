package com.hqy.cloud.service;

import com.hqy.common.bind.MessageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-07-19 16:57
 */
@Component
@FeignClient(value = "CLOUD-PROVIDER-DEMO") //需要远程调用的服务名称
public interface PaymentFeignService {

    @GetMapping(value = "/payment/get/{id}")
    MessageResponse getPaymentById(@PathVariable("id")Long id);
}
