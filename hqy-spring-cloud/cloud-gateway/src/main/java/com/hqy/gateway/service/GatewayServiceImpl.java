package com.hqy.gateway.service;

import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/1 11:04
 */
@Service
public class GatewayServiceImpl implements GateWayService  {


    @Override
    public void test() {
        System.out.println("调用远程服务@@@ ");
    }
}
