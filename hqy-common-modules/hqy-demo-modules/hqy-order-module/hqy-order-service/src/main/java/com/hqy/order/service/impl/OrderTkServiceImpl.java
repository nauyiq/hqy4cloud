package com.hqy.order.service.impl;

import com.hqy.base.BaseDao;
import com.hqy.base.impl.BaseTkServiceImpl;
import com.hqy.common.entity.order.Order;
import com.hqy.order.dao.OrderDao;
import com.hqy.order.service.OrderTkService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/23 16:49
 */
@Service
public class OrderTkServiceImpl extends BaseTkServiceImpl<Order, Long> implements OrderTkService {

    @Resource
    private OrderDao dao;

    @Override
    public BaseDao<Order, Long> selectDao() {
        return dao;
    }
}
