package com.hqy.cloud.coll.service;

import com.github.pagehelper.PageInfo;
import com.hqy.cloud.db.tk.BaseTkService;
import com.hqy.cloud.coll.entity.RPCFlowRecord;
import com.hqy.cloud.rpc.thrift.struct.PageStruct;

/**
 * RPCFlowRecordService.
 * @author qiyuan.hong
 * @date 2022-03-17 21:26
 */
public interface RPCFlowRecordService extends BaseTkService<RPCFlowRecord, Long> {

    /**
     * 分页查询
     * @param caller     rpc调用者
     * @param provider   rpc提供者
     * @param pageStruct 分页参数
     * @return           PageInfo for RPCFlowRecord.
     */
    PageInfo<RPCFlowRecord> queryPage(String caller, String provider, PageStruct pageStruct);
}
