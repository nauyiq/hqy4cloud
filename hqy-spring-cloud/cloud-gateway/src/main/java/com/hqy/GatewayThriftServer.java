package com.hqy;

import com.hqy.fundation.common.rpc.api.RPCService;
import com.hqy.gateway.service.GatewayServiceImpl;
import com.hqy.rpc.api.AbstractThriftServer;
import com.hqy.util.spring.SpringContextHolder;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * 注册需要暴露的rpc接口.（由父类进行统一的注册）
 * @author qy
 * @date 2021-09-24 17:47
 */
@Component
public class GatewayThriftServer extends AbstractThriftServer  {

    @Override
    public List<RPCService> getServiceList4Register() {

        List<RPCService> rpcServices = new ArrayList<>();
        if (CollectionUtils.isEmpty(rpcServices)) {
            GatewayServiceImpl gatewayService = SpringContextHolder.getBean(GatewayServiceImpl.class);
            rpcServices.add(gatewayService);
        }

        return rpcServices;
    }
}

