package com.hqy.cloud.admin.service.impl;

import com.hqy.cloud.admin.service.RequestAdminThrottleService;
import com.hqy.cloud.common.bind.DataResponse;
import com.hqy.cloud.common.bind.MessageResponse;
import com.hqy.cloud.common.result.CommonResultCode;
import com.hqy.coll.service.CollPersistService;
import com.hqy.coll.struct.PageThrottledBlockResultStruct;
import com.hqy.rpc.nacos.client.starter.RPCClient;
import com.hqy.rpc.thrift.struct.PageStruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/5 15:12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RequestAdminThrottleServiceImpl implements RequestAdminThrottleService {

    @Override
    public DataResponse getPageThrottledHistory(String throttleBy, String ip, String uri, Integer current, Integer size) {
        //RPC获取分页数据.
        CollPersistService collPersistService = RPCClient.getRemoteService(CollPersistService.class);
        PageThrottledBlockResultStruct pageResultStruct = collPersistService.getPageThrottledBlock(throttleBy, ip, uri, new PageStruct(current, size));
        if (pageResultStruct == null) {
            pageResultStruct = new PageThrottledBlockResultStruct();
        }
        return CommonResultCode.dataResponse(pageResultStruct);
    }

    @Override
    public MessageResponse deleteThrottledHistory(Long id) {
        CollPersistService collPersistService = RPCClient.getRemoteService(CollPersistService.class);
        collPersistService.deleteThrottledBlockHistory(id);
        return CommonResultCode.messageResponse();
    }
}
