package com.hqy.order.dao;

import com.hqy.base.BaseDao;
import com.hqy.common.entity.order.OrderMessageRecord;
import org.springframework.stereotype.Repository;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/10 17:05
 */
@Repository
public interface OrderMessageRecordDao extends BaseDao<OrderMessageRecord, Long> {
}
