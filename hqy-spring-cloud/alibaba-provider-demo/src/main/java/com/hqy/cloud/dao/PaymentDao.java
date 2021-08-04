package com.hqy.cloud.dao;

import com.hqy.cloud.entity.Payment;
import com.hqy.dao.base.BaseDao;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author qy
 * @create 2021/7/15 23:53
 */
@Repository
public interface PaymentDao extends BaseDao<Payment, Long> {

    int insertById(Payment payment);

    Payment selectById(@Param("id") Long id);

}
