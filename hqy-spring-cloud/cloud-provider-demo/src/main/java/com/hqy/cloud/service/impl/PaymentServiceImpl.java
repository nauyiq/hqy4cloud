package com.hqy.cloud.service.impl;

import com.hqy.cloud.dao.PaymentDao;
import com.hqy.cloud.entity.Payment;
import com.hqy.cloud.service.PaymentService;

import javax.annotation.Resource;

/**
 * @author qy
 * @create 2021/7/15 23:56
 */
public class PaymentServiceImpl implements PaymentService {

    @Resource
    private PaymentDao paymentDao;

    @Override
    public int insert(Payment payment) {
        return paymentDao.insert(payment);
    }

    @Override
    public Payment selectById(Long id) {
        return paymentDao.selectById(id);
    }

}
