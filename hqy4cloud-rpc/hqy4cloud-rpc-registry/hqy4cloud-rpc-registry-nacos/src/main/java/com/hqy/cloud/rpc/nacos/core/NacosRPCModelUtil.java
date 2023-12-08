package com.hqy.cloud.rpc.nacos.core;

import com.hqy.cloud.common.base.lang.ActuatorNode;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.rpc.core.Environment;
import com.hqy.cloud.rpc.model.PubMode;
import com.hqy.cloud.rpc.model.RPCModel;
import com.hqy.cloud.rpc.model.RPCServerAddress;
import com.hqy.cloud.rpc.model.RegistryInfo;
import com.hqy.cloud.rpc.nacos.node.Metadata;

import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/5 10:22
 */
public class NacosRPCModelUtil {

    public static RPCModel buildRPCModel(String application, int port, String group, RegistryInfo registryInfo,
                                         RPCServerAddress rpcServerAddress, Map<String, String> attachment) {
        return new RPCModel(application, port, group, registryInfo, rpcServerAddress, attachment);
    }

    public static Metadata buildMetadata(int wight, String hashFactor, ActuatorNode actuatorNode,
                                         RPCServerAddress rpcServerAddress, Environment environment, Map<String, String> attachment) {
        return new Metadata(wight, getPubMode(environment).value,  rpcServerAddress, hashFactor, actuatorNode, attachment);
    }

    public static PubMode getPubMode(Environment environment) {
        if (environment.isTestEnvironment()) {
            CommonSwitcher.ENABLE_GRAY_MECHANISM.setStatus(true);
            return PubMode.WHITE;
        } else if (environment.isDevEnvironment() || environment.isUatEnvironment()) {
            CommonSwitcher.ENABLE_GRAY_MECHANISM.setStatus(false);
            return PubMode.GRAY;
        } else {
            CommonSwitcher.ENABLE_GRAY_MECHANISM.setStatus(true);
            return PubMode.WHITE;
        }
    }

}
