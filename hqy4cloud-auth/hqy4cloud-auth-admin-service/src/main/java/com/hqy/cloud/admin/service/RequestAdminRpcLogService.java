package com.hqy.cloud.admin.service;

import com.hqy.cloud.common.bind.R;
import com.hqy.coll.struct.PageRpcExceptionRecordStruct;
import com.hqy.coll.struct.PageRpcFlowRecordStruct;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/9 14:15
 */
public interface RequestAdminRpcLogService {

    /**
     * 分页查询RPC流量日志
     * @param caller   调用方
     * @param provider 请求方
     * @param current  当前页
     * @param size     页行数
     * @return         R.
     */
    R<PageRpcFlowRecordStruct> queryRpcFlowPage(String caller, String provider, Integer current, Integer size);

    /**
     * 删除rpc流量日志
     * @param id id
     * @return   R.
     */
    R<Boolean> deleteRpcFlowRecord(Long id);

    /**
     * 分页查询RPC错误日志
     * @param application      服务名
     * @param serviceClassName rpc class名字
     * @param type             类型
     * @param current          当前页
     * @param size             页行数
     * @return                 R.
     */
    R<PageRpcExceptionRecordStruct> queryRpcErrorPage(String application, String serviceClassName, Integer type, Integer current, Integer size);


    /**
     * 删除rpc异常日志
     * @param id id.
     * @return   R.
     */
    R<Boolean> deleteRpcExceptionRecord(Long id);
}
