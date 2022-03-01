package com.hqy;

import com.hqy.fundation.common.rpc.api.RPCService;
import com.hqy.rpc.api.AbstractThriftServer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 注册需要暴露的rpc接口.（由父类进行统一的注册）
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/1 17:25
 */
@Component
public class CommonCollectorThriftServer extends AbstractThriftServer {

    @Override
    public List<RPCService> getServiceList4Register() {
        List<RPCService> rpcServices = new ArrayList<>();



        return rpcServices;
    }
}
