package com.hqy.cloud.auth.admin.service.impl;

import com.hqy.cloud.auth.admin.service.RequestAdminRpcLogService;
import com.hqy.cloud.coll.service.RpcLogRemoteService;
import com.hqy.cloud.coll.struct.PageRpcExceptionRecordStruct;
import com.hqy.cloud.coll.struct.PageRpcFlowRecordStruct;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.rpc.starter.client.RpcClient;
import com.hqy.cloud.rpc.thrift.struct.PageStruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/9 14:15
 */
@Service
@RequiredArgsConstructor
public class RequestAdminRpcLogServiceImpl implements RequestAdminRpcLogService {


    @Override
    public R<PageRpcFlowRecordStruct> queryRpcFlowPage(String caller, String provider, Integer current, Integer size) {
        RpcLogRemoteService service = RpcClient.getRemoteService(RpcLogRemoteService.class);
        PageRpcFlowRecordStruct pageRpcFlowRecordStruct = service.pageRpcFlowLog(caller, provider, new PageStruct(current, size));
        return R.ok(pageRpcFlowRecordStruct);
    }

    @Override
    public R<Boolean> deleteRpcFlowRecord(Long id) {
        RpcLogRemoteService service = RpcClient.getRemoteService(RpcLogRemoteService.class);
        service.deleteRpcFlowLogRecord(id);
        return R.ok();
    }

    @Override
    public R<PageRpcExceptionRecordStruct> queryRpcErrorPage(String application, String serviceClassName, Integer type, Integer current, Integer size) {
        RpcLogRemoteService service = RpcClient.getRemoteService(RpcLogRemoteService.class);
        PageRpcExceptionRecordStruct pageRpcExceptionRecordStruct = service.pageRpcErrorLog(application, serviceClassName, type, new PageStruct(current, size));
        return R.ok(pageRpcExceptionRecordStruct);
    }

    @Override
    public R<Boolean> deleteRpcExceptionRecord(Long id) {
        RpcLogRemoteService service = RpcClient.getRemoteService(RpcLogRemoteService.class);
        service.deleteRpcExceptionRecord(id);
        return R.ok();
    }
}
