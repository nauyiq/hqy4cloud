package com.hqy;

import com.hqy.base.common.rpc.api.RPCService;
import com.hqy.rpc.api.AbstractThriftServer;
import com.hqy.order.common.service.OrderRemoteService;
import com.hqy.util.spring.SpringContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 11:08
 */
@Component
public class OrderThriftServer extends AbstractThriftServer {

    @Override
    public List<RPCService> getServiceList4Register() {
        OrderRemoteService orderRemoteService = SpringContextHolder.getBean(OrderRemoteService.class);
        return Collections.singletonList(orderRemoteService);
    }
}
