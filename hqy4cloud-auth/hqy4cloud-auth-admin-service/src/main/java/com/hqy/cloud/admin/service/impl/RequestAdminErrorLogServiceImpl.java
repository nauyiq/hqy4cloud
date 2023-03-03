package com.hqy.cloud.admin.service.impl;

import com.hqy.cloud.admin.service.RequestAdminErrorLogService;
import com.hqy.cloud.common.bind.DataResponse;
import com.hqy.cloud.common.bind.MessageResponse;
import com.hqy.cloud.common.result.CommonResultCode;
import com.hqy.coll.service.ExceptionCollectionService;
import com.hqy.coll.struct.PageExceptionLogStruct;
import com.hqy.rpc.nacos.client.starter.RPCClient;
import com.hqy.rpc.thrift.struct.PageStruct;
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
    public DataResponse pageErrorLog(String serviceName, String type, String environment, String exceptionClass, String ip, String url, Integer current, Integer size) {
        ExceptionCollectionService collectionService = RPCClient.getRemoteService(ExceptionCollectionService.class);
        PageExceptionLogStruct pageExceptionLogStruct = collectionService.queryPage(serviceName, type, environment, exceptionClass, ip, url, new PageStruct(current, size));
        return CommonResultCode.dataResponse(pageExceptionLogStruct);
    }

    @Override
    public MessageResponse deleteErrorLog(Long id) {
        ExceptionCollectionService collectionService = RPCClient.getRemoteService(ExceptionCollectionService.class);
        collectionService.deleteErrorLog(id);
        return CommonResultCode.messageResponse();
    }
}
