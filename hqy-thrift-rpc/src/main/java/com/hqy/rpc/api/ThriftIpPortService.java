package com.hqy.rpc.api;

import com.hqy.fundation.common.base.project.UsingIpPort;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/2/21 17:36
 */
public interface ThriftIpPortService {

    /**
     * 获取当前项目的端口信息
     * @return
     */
    UsingIpPort getUsingIpPort();
    
    
}
