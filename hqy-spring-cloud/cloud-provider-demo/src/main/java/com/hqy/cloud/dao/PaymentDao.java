package com.hqy.cloud.dao;

import com.hqy.cloud.entity.Payment;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author qy
 * @create 2021/7/15 23:53
 */
@Repository
public interface PaymentDao {

    int insert(Payment payment);

    Payment selectById(@Param("id") Long id);

}
