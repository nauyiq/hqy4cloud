package com.hqy;

import com.hqy.account.service.AccountInfoService;
import com.hqy.fundation.common.rpc.api.RPCService;
import com.hqy.rpc.api.AbstractThriftServer;
import com.hqy.util.spring.SpringContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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

        List<RPCService> rpcServices = new ArrayList<>();
        AccountInfoService accountInfoService = SpringContextHolder.getBean(AccountInfoService.class);
        rpcServices.add(accountInfoService);
        return rpcServices;
    }
}
