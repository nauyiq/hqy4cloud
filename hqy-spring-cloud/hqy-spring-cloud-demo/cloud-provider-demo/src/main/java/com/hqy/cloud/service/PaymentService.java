package com.hqy.cloud.service;

import com.hqy.cloud.entity.Payment;
import org.apache.ibatis.annotations.Param;

/**
 * @author qy
 * @create 2021/7/15 23:56
 */
public interface PaymentService {

    int insert(Payment payment);

    Payment selectById(@Param("id") Long id);
}
