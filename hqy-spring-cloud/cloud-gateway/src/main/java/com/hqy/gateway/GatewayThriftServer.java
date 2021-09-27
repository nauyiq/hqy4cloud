package com.hqy.gateway;

import com.hqy.fundation.common.rpc.api.RPCService;
import com.hqy.rpc.api.AbstractThriftServer;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-09-24 17:47
 */
@Component
public class GatewayThriftServer extends AbstractThriftServer {


    @Override
    public List<RPCService> getServiceList4Register() {

        List<RPCService> rpcServices = new ArrayList<>();
        if (CollectionUtils.isEmpty(rpcServices)) {

        }

        return rpcServices;
    }
}
