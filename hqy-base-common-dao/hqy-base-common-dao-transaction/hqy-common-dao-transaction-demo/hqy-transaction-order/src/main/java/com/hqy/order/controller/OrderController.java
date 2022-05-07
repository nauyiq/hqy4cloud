package com.hqy.order.controller;

import com.hqy.base.common.bind.MessageResponse;
import com.hqy.order.service.OrderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/11 14:52
 */
@RestController
public class OrderController {

    @Resource
    OrderService orderService;

    @PostMapping("/order")
    public MessageResponse order(Long storageId, Integer count, boolean tcc) {
        if (storageId == null) {
            storageId = 1L;
        }
        if (count == null || count <= 0) {
            count = 1;
        }
        if (tcc) {
            return orderService.tccOrder(storageId, count);
        }
        return orderService.order(storageId, count);
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


}
