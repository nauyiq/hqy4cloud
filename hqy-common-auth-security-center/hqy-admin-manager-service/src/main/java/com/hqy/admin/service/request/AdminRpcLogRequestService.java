package com.hqy.admin.service.request;

import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.bind.MessageResponse;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/9 14:15
 */
public interface AdminRpcLogRequestService {

    /**
     * 分页查询RPC流量日志
     * @param caller   调用方
     * @param provider 请求方
     * @param current  当前页
     * @param size     页行数
     * @return         DataResponse.
     */
    DataResponse queryRpcFlowPage(String caller, String provider, Integer current, Integer size);

    /**
     * 删除rpc流量日志
     * @param id id
     * @return   MessageResponse.
     */
    MessageResponse deleteRpcFlowRecord(Long id);

    /**
     * 分页查询RPC错误日志
     * @param application      服务名
     * @param serviceClassName rpc class名字
     * @param type             类型
     * @param current          当前页
     * @param size             页行数
     * @return                 DataResponse.
     */
    DataResponse queryRpcErrorPage(String application, String serviceClassName, Integer type, Integer current, Integer size);


    /**
     * 删除rpc异常日志
     * @param id id.
     * @return   MessageResponse.
     */
    MessageResponse deleteRpcExceptionRecord(Long id);
}
