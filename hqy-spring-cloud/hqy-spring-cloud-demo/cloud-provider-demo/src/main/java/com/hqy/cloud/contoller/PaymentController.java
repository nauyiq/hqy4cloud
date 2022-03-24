package com.hqy.cloud.contoller;

import cn.hutool.core.util.IdUtil;
import com.hqy.cloud.entity.Payment;
import com.hqy.cloud.service.PaymentService;
import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.bind.MessageResponse;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author qy
 * @Create 2021/4/6 23:37
 */
@RestController
@Slf4j
@RefreshScope
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

    /**
     * 服务降级...
     * Hystrix指定方法降级 fallbackMethod = getPaymentByDefault
     * @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="3000") 超过3秒表示执行降级方法
     * @param id
     * @return
     * @throws InterruptedException
     */
    @GetMapping(value = "/payment/get/{id}")
    @HystrixCommand(fallbackMethod = "getPaymentByDefault",commandProperties = {
            @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="3000")
    })
    public MessageResponse getPaymentById(@PathVariable("id") Long id) throws InterruptedException {
        Payment payment = paymentService.selectById(id);
        log.info("*****查询结果:{}", payment);
        if (payment != null) {
            return new DataResponse(true, "查询成功, 端口号:" + serverPort, 200, payment);
        } else {
            return new MessageResponse(false, "没有对应记录,查询ID: " + id, 400);
        }
    }

    public MessageResponse getPaymentByDefault(Long id) {
        return new MessageResponse(false, "server interval error, try again latter.", 500);
    }


    /**
     * 服务熔断
     * @param id
     * @return
     */
    @HystrixCommand(fallbackMethod = "getPaymentByDefault",commandProperties = {
            @HystrixProperty(name = "circuitBreaker.enabled",value = "true"), //是否开启断路器
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold",value = "10"), //请求次数
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds",value = "10000"), //时间窗口值
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage",value = "50"), //失败率
    })
    @GetMapping(value = "/payment/circuitBreaker/{id}")
    public MessageResponse paymentCircuitBreaker(@PathVariable("id") Long id) {
        if (id < 0) {
            throw new RuntimeException("server interval error, try again latter.");
        }
        String uuid = IdUtil.simpleUUID();
        return new MessageResponse(true, "success, uuid:" + uuid, 0);

    }

}
