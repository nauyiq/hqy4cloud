package com.hqy.cloud.contoller;

import com.hqy.cloud.entity.Payment;
import com.hqy.cloud.service.PaymentService;
import com.hqy.common.bind.DataResponse;
import com.hqy.common.bind.MessageResponse;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @Author qy
 * @Create 2021/4/6 23:37
 */
@RestController
@Slf4j
public class PaymentController {

    @Resource
    private PaymentService paymentService;

    @Value("${server.port}")
    private String serverPort;

    @PostMapping(value = "/payment")
    public MessageResponse create(@RequestBody Payment payment) {
        int result = paymentService.insert(payment);
        log.info("*****插入操作返回结果:" + result);

        if (result > 0) {
            return new MessageResponse(true, "插入数据库成功", 200);
        } else {
            return new MessageResponse(false, "插入数据库失败", 400);
        }
    }


    @GetMapping(value = "/payment/get/{id}")
    @HystrixCommand(fallbackMethod = "getPaymentByDefault", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value="3000")
    })
    public MessageResponse getPaymentById(@PathVariable("id") Long id) throws InterruptedException {
        TimeUnit.SECONDS.sleep(5);
        Payment payment = paymentService.selectById(id);
        log.info("*****查询结果:{}", payment);
        if (payment != null) {
            return new DataResponse(true, "查询成功, 端口号:" + serverPort, 200, payment);
        } else {
            return new MessageResponse(false, "没有对应记录,查询ID: " + id, 400);
        }
    }

    public MessageResponse getPaymentByDefault() {
        return new MessageResponse(false, "try again latter", 500);
    }


}
