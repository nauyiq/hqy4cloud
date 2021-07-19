package com.hqy.cloud.controller;

import com.hqy.cloud.service.PaymentFeignService;
import com.hqy.common.bind.MessageResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-07-19 17:02
 */
@RestController
public class ConsumerDemoController {

    @Resource
    private PaymentFeignService paymentFeignService;

    @GetMapping(value = "/consumer/payment/get/{id}")
    public MessageResponse getPaymentById(@PathVariable("id") Long id) {
        return paymentFeignService.getPaymentById(id);
    }




}
