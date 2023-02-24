package com.hqy.collector.dao;

import com.hqy.cloud.tk.BaseTkMapper;
import com.hqy.collector.entity.RPCExceptionRecord;
import org.springframework.stereotype.Repository;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/5 15:25
 */
@Repository
public interface RPCExceptionRecordTkMapper extends BaseTkMapper<RPCExceptionRecord, Long> {
}
