package com.hqy;

import com.hqy.base.common.rpc.api.RPCService;
import com.hqy.order.common.service.AccountRemoteService;
import com.hqy.rpc.api.AbstractThriftServer;
import com.hqy.util.spring.SpringContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 13:47
 */
@Component
public class AccountThriftServer extends AbstractThriftServer {

    @Override
    public List<RPCService> getServiceList4Register() {
        AccountRemoteService remoteService = SpringContextHolder.getBean(AccountRemoteService.class);
        return Collections.singletonList(remoteService);
    }
}
