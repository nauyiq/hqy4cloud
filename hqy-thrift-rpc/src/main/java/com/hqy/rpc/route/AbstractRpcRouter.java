package com.hqy.rpc.route;

import com.hqy.fundation.common.base.project.UsingIpPort;

import java.util.List;

/**
 * @author qy
 * @date 2021-08-13 11:25
 */
public abstract class AbstractRpcRouter implements RpcRouter {

    /**
     * 灰度可连接节点列表
     */
    private List<UsingIpPort> addressGray;

    /**
     * 白度可连接节点列表
     */
    private List<UsingIpPort> addressWhite;


    @Override
    public List<UsingIpPort> getGrayProviders() {
        return addressGray;
    }

    @Override
    public List<UsingIpPort> getWhiteProviders() {
        return addressWhite;
    }

    @Override
    public void setGrayProviders(List<UsingIpPort> usingPorts) {
        this.addressGray = usingPorts;
    }

    @Override
    public void setWhiteProviders(List<UsingIpPort> usingPorts) {
       this.addressWhite = usingPorts;
    }

    @Override
    public List<UsingIpPort> getAllProviders() {
        return null;
    }
}
