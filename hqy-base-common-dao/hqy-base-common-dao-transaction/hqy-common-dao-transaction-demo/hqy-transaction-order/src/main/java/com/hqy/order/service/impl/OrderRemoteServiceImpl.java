package com.hqy.order.service.impl;

import com.hqy.order.common.entity.Order;
import com.hqy.order.service.OrderService;
import com.hqy.rpc.api.AbstractRPCService;
import com.hqy.order.common.service.OrderRemoteService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 11:14
 */
@Service
public class OrderRemoteServiceImpl extends AbstractRPCService implements OrderRemoteService {

    @Resource
    private OrderService orderService;

    @Override
    public Long order(Long productId, int count, String money) {
        Order order = new Order();
        order.setProductId(productId);
        order.setCount(count);
        order.setMoney(new BigDecimal(money));
        return orderService.insertReturnPk(order);
    }
}
