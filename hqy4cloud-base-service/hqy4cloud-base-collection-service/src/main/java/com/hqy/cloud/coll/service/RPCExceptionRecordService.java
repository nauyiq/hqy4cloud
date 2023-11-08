package com.hqy.cloud.coll.service;

import com.github.pagehelper.PageInfo;
import com.hqy.cloud.db.tk.BaseTkService;
import com.hqy.cloud.coll.entity.RPCExceptionRecord;
import com.hqy.cloud.rpc.thrift.struct.PageStruct;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/5 15:24
 */
public interface RPCExceptionRecordService extends BaseTkService<RPCExceptionRecord, Long> {

    /**
     * 分页查询RPC异常日志
     * @param application      服务名
     * @param serviceClassName rpc类名
     * @param type             类型
     * @param struct           分页参数
     * @return                 PageInfo for RPCExceptionRecord
     */
    PageInfo<RPCExceptionRecord> queryPage(String application, String serviceClassName, Integer type, PageStruct struct);
}
