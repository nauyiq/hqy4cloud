package com.hqy;

import com.hqy.fundation.common.rpc.api.RPCService;
import com.hqy.rpc.api.AbstractThriftServer;
import com.hqy.util.spring.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * 注册需要暴露的rpc接口.（由父类进行统一的注册）
 * @author qy
 * @date 2021-09-24 17:47
 */
@Slf4j
@Component
public class GatewayThriftServer extends AbstractThriftServer {
    @Override
    public List<RPCService> getServiceList4Register() {
        log.info("@@@ 当前服务为RPC的消费者, 无需注册ThriftServer");
        return new ArrayList<>();
    }
}

