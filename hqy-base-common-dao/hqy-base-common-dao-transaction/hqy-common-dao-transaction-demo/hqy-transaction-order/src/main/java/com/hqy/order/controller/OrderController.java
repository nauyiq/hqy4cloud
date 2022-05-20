package com.hqy.order.controller;

import com.hqy.base.common.bind.MessageResponse;
import com.hqy.order.service.OrderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 分布式事务模拟下单方案落地.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/11 14:52
 */
@RestController
public class OrderController {

    @Resource
    OrderService orderService;

    @PostMapping("/seata/at/order")
    public MessageResponse seataATOrder() {
        return orderService.seataATOrder(1L, 3);
    }


    @PostMapping("/seata/tcc/order")
    public MessageResponse seataTccOrder() {
        return orderService.seataTccOrder(1L, 3);
    }



    @PostMapping("/mq/order")
    public MessageResponse order(Long storageId, Integer count) {
        if (storageId == null) {
            storageId = 1L;
        }
        if (count == null || count <= 0) {
            count = 1;
        }
        return orderService.mqOrderDemo(storageId, count);
    }


    @PostMapping("/mq/kafka/order")
    public MessageResponse kafkaOrder(Long storageId, Integer count) {
        if (storageId == null) {
            storageId = 1L;
        }
        if (count == null || count <= 0) {
            count = 1;
        }
        return orderService.kafkaOrder(storageId, count);
    }

    @PostMapping
    public MessageResponse rocketMqOrder(Long storageId, Integer count) {
        if (storageId == null) {
            storageId = 1L;
        }
        if (count == null || count <= 0) {
            count = 1;
        }
        return orderService.rocketMqOrder(storageId, count);
    }



}
