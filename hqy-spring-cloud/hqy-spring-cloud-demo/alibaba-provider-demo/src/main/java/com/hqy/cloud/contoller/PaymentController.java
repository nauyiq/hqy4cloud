package com.hqy.cloud.contoller;

import com.hqy.cloud.entity.Payment;
import com.hqy.cloud.service.PaymentService;
import com.hqy.fundation.common.bind.DataResponse;
import com.hqy.fundation.common.bind.MessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

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

    @GetMapping("/payment/{id}")
    public MessageResponse getPaymentById(@PathVariable("id") Long id) throws InterruptedException {
        Payment payment = null;
        log.info("*****查询结果:{}", payment);
        if (payment != null) {
            return new DataResponse(true, "查询成功, 端口号:" + serverPort, 200, payment);
        } else {
            return new MessageResponse(false, "没有对应记录,查询ID: " + id, 400);
        }
    }

}
