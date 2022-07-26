package com.hqy.order.dao;

import com.hqy.base.BaseDao;
import com.hqy.common.entity.order.Order;
import org.springframework.stereotype.Repository;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 10:48
 */
@Repository
public interface OrderDao extends BaseDao<Order, Long> {

}
