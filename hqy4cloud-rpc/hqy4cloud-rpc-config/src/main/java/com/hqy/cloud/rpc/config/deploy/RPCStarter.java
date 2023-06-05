package com.hqy.cloud.rpc.config.deploy;

import com.hqy.cloud.rpc.service.RPCModelService;

/**
 * Rpc启动器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/14 11:02
 */
public interface RPCStarter extends RPCModelService {

    /**
     * start rpc.
     * @return  future.
     */
    boolean start();

}
