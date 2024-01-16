package com.hqy.cloud.admin.service.impl;

import com.hqy.cloud.admin.service.RequestAdminThrottleService;
import com.hqy.cloud.coll.service.CollPersistService;
import com.hqy.cloud.coll.struct.PageThrottledBlockResultStruct;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.rpc.starter.client.RpcClient;
import com.hqy.cloud.rpc.thrift.struct.PageStruct;
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
    public R<PageThrottledBlockResultStruct> getPageThrottledHistory(String throttleBy, String ip, String uri, Integer current, Integer size) {
        //RPC获取分页数据.
        CollPersistService collPersistService = RpcClient.getRemoteService(CollPersistService.class);
        PageThrottledBlockResultStruct pageResultStruct = collPersistService.getPageThrottledBlock(throttleBy, ip, uri, new PageStruct(current, size));
        if (pageResultStruct == null) {
            pageResultStruct = new PageThrottledBlockResultStruct();
        }
        return R.ok(pageResultStruct);
    }

    @Override
    public  R<Boolean> deleteThrottledHistory(Long id) {
        CollPersistService collPersistService = RpcClient.getRemoteService(CollPersistService.class);
        collPersistService.deleteThrottledBlockHistory(id);
        return R.ok();
    }
}
