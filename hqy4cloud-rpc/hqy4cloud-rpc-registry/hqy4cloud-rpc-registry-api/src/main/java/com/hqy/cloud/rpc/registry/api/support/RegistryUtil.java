package com.hqy.cloud.rpc.registry.api.support;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.rpc.model.RPCModel;
import com.hqy.cloud.rpc.model.RegistryInfo;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.IpUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/5 11:13
 */
public class RegistryUtil {

    public static RegistryInfo buildRegistryInfo(RPCModel rpcModel, String registryName) {
        RegistryInfo registryInfo = rpcModel.getRegistryInfo();
        if (Objects.nonNull(registryInfo)) {
            return registryInfo;
        }
        String registryAddress = rpcModel.getRegistryAddress();
        return buildRegistryInfo(registryAddress, registryName);
    }

    public static RegistryInfo buildRegistryInfo(String registryAddress, String registryName) {
        int port;
        String hostAddr;
        String[] hostAndPort = registryAddress.split(StrUtil.COLON);
        if (hostAndPort.length == 1) {
            //if not ip string. try to analysis host.
            hostAddr = getIpByHost(registryAddress);
            port = 0;
        } else {
            port = Integer.parseInt(hostAndPort[1]);
            hostAddr = hostAndPort[0];
            if (!IpUtil.isIP(hostAddr)) {
                //if not ip string. try to analysis host.
                hostAddr = getIpByHost(hostAddr);
            }
        }
        return new RegistryInfo(registryName, hostAddr, port, registryAddress);
    }

    public static String getIpByHost(String serverAddr) {
        String ip = NetUtil.getIpByHost(serverAddr);
        AssertUtil.isTrue(StringUtils.isNotBlank(ip) && IpUtil.isIP(ip), "Invalid input serverAddr, addr: " + serverAddr);
        return ip;
    }

}
