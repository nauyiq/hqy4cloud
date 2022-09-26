package com.hqy.collector.dao;

import com.hqy.base.BaseDao;
import com.hqy.collector.entity.RPCFlowRecord;
import org.springframework.stereotype.Repository;

/**
 * @author qiyuan.hong
 * @date 2022-03-17 21:26
 */
@Repository
public interface RPCMinuteFlowRecordDao extends BaseDao<RPCFlowRecord, Long> {
}
