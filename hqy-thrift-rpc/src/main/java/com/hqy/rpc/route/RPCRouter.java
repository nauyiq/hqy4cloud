package com.hqy.rpc.route;

import com.hqy.fundation.common.base.project.UsingIpPort;

import java.util.List;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-13 11:16
 */
public interface RPCRouter {

    List<UsingIpPort>  getGrayProviders();

    List<UsingIpPort> getWhiteProviders();

    void setGrayProviders(List<UsingIpPort> ports);

    void setWhiteProviders(List<UsingIpPort> ports);

    List<UsingIpPort> getAllProviders();

}
