package com.hqy.rpc.route;

import com.hqy.rpc.regist.UsingIpPort;

import java.util.List;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-13 11:25
 */
public abstract class AbstractRPCRouter implements RPCRouter {

    private List<UsingIpPort> addressGray;

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
    public void setGrayProviders(List<UsingIpPort> grayProviders) {
        this.addressGray = grayProviders;
    }

    @Override
    public void setWhiteProviders(List<UsingIpPort> whiteProviders) {
       this.addressWhite = whiteProviders;
    }

    @Override
    public List<UsingIpPort> getAllProviders() {
        return null;
    }
}
