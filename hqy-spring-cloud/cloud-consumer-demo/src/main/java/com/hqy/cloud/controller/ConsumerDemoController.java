package com.hqy.cloud.controller;

import com.hqy.cloud.service.PaymentFeignService;
import com.hqy.common.bind.DataResponse;
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


    /*@HystrixCommand(fallbackMethod = "getPaymentByDefault",commandProperties = {
            @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="3000")
    })*/
    @GetMapping(value = "/consumer/payment/get/{id}")
    public DataResponse getPaymentById(@PathVariable("id") Long id) {
        return paymentFeignService.getPaymentById(id);
    }

    public DataResponse getPaymentByDefault(Long id) {
        return new DataResponse(false, "try again latter, id=" + id, 500);
    }




}
