package com.hqy.cloud.admin.service.impl;

import com.hqy.cloud.admin.service.RequestAdminErrorLogService;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.coll.service.ExceptionCollectionService;
import com.hqy.cloud.coll.struct.PageExceptionLogStruct;
import com.hqy.cloud.rpc.nacos.client.RPCClient;
import com.hqy.cloud.rpc.thrift.struct.PageStruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/6 15:41
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RequestAdminErrorLogServiceImpl implements RequestAdminErrorLogService {

    @Override
    public R<PageExceptionLogStruct> pageErrorLog(String serviceName, String type, String environment, String exceptionClass, String ip, String url, Integer current, Integer size) {
        ExceptionCollectionService collectionService = RPCClient.getRemoteService(ExceptionCollectionService.class);
        PageExceptionLogStruct pageExceptionLogStruct = collectionService.queryPage(serviceName, type, environment, exceptionClass, ip, url, new PageStruct(current, size));
        return R.ok(pageExceptionLogStruct);
    }

    @Override
    public R<Boolean> deleteErrorLog(Long id) {
        ExceptionCollectionService collectionService = RPCClient.getRemoteService(ExceptionCollectionService.class);
        collectionService.deleteErrorLog(id);
        return R.ok();
    }
}
