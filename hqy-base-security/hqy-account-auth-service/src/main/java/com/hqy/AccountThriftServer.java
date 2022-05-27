package com.hqy;

import com.hqy.account.service.AccountRemoteThriftService;
import com.hqy.base.common.rpc.api.RPCService;
import com.hqy.rpc.api.AbstractThriftServer;
import com.hqy.util.spring.SpringContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 所有需要暴露出RPC的服务 必须继承AbstractNacosClientWrapper类
 * @author qiyuan.hong
 * @date 2022-03-10 22:04
 */
@Component
public class AccountThriftServer extends AbstractThriftServer {

    @Override
    public List<RPCService> getServiceList4Register() {
        AccountRemoteThriftService accountRemoteThriftService = SpringContextHolder.getBean(AccountRemoteThriftService.class);
        return Collections.singletonList(accountRemoteThriftService);
    }
}
