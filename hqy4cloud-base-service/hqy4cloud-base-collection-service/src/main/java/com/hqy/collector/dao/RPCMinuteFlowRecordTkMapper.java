package com.hqy.collector.dao;

import com.hqy.cloud.tk.BaseTkMapper;
import com.hqy.collector.entity.RPCFlowRecord;
import org.springframework.stereotype.Repository;

/**
 * @author qiyuan.hong
 * @date 2022-03-17 21:26
 */
@Repository
public interface RPCMinuteFlowRecordTkMapper extends BaseTkMapper<RPCFlowRecord, Long> {
}
