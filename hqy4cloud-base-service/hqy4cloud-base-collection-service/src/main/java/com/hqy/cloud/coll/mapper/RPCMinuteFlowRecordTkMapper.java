package com.hqy.cloud.coll.mapper;

import com.hqy.cloud.db.tk.BaseTkMapper;
import com.hqy.cloud.coll.entity.RPCFlowRecord;
import org.springframework.stereotype.Repository;

/**
 * @author qiyuan.hong
 * @date 2022-03-17 21:26
 */
@Repository
public interface RPCMinuteFlowRecordTkMapper extends BaseTkMapper<RPCFlowRecord, Long> {
}
