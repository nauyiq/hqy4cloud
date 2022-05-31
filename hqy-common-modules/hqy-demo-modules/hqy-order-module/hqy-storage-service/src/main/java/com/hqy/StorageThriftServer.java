package com.hqy;

import com.hqy.base.common.rpc.api.RPCService;
import com.hqy.common.service.StorageRemoteService;
import com.hqy.rpc.api.AbstractThriftServer;
import com.hqy.util.spring.SpringContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 14:18
 */
@Component
public class StorageThriftServer extends AbstractThriftServer {

    @Override
    public List<RPCService> getServiceList4Register() {
        StorageRemoteService storageService = SpringContextHolder.getBean(StorageRemoteService.class);
        return Collections.singletonList(storageService);
    }
}
