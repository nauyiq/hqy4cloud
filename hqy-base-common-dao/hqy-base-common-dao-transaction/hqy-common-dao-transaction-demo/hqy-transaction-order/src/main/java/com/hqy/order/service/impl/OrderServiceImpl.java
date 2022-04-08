package com.hqy.order.service.impl;

import com.hqy.base.BaseDao;
import com.hqy.base.impl.BaseTkServiceImpl;
import com.hqy.order.common.entity.Order;
import com.hqy.order.dao.OrderDao;
import com.hqy.order.service.OrderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 10:46
 */
@Service
public class OrderServiceImpl extends BaseTkServiceImpl<Order, Long> implements OrderService {

    @Resource
    private OrderDao orderDao;

    @Override
    public BaseDao<Order, Long> selectDao() {
        return orderDao;
    }
}
