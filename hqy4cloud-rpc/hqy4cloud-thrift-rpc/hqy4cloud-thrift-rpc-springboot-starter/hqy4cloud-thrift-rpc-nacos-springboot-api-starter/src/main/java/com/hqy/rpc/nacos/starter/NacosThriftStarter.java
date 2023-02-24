package com.hqy.rpc.nacos.starter;

import cn.hutool.core.net.NetUtil;
import com.hqy.cloud.common.base.lang.ActuatorNodeEnum;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.base.lang.exception.RpcException;
import com.hqy.cloud.common.base.project.UsingIpPort;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.rpc.common.PubMode;
import com.hqy.rpc.common.RPCServerAddress;
import com.hqy.rpc.common.config.EnvironmentConfig;
import com.hqy.rpc.common.support.RPCModel;
import com.hqy.rpc.common.support.RegistryInfo;
import com.hqy.rpc.registry.nacos.node.Metadata;
import com.hqy.rpc.registry.nacos.util.NacosConfigurationUtils;
import com.hqy.util.AssertUtil;
import com.hqy.util.IpUtil;
import com.hqy.util.JsonUtil;
import com.hqy.util.spring.ProjectContextInfo;
import com.hqy.util.spring.SpringContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/13 17:44
 */
public abstract class NacosThriftStarter implements RPCStarter {
    private static final Logger log = LoggerFactory.getLogger(NacosThriftStarter.class);

    private final String application;

    private final int serverPort;

    private final String serverAddress;

    private final int wight;

    private final String hashFactor;

    private final String group;

    private RPCModel rpcModel;

    private final Metadata metadata;

    private final ActuatorNodeEnum actuatorType;

    private final PubMode pubMode;

    public NacosThriftStarter(String application, int serverPort, String serverAddress, int wight, ActuatorNodeEnum actuatorType, String hashFactor, String group) {
        this.application = application;
        this.serverPort = serverPort;
        this.serverAddress = serverAddress;
        this.pubMode = initPubMode();
        this.wight = wight;
        this.hashFactor = hashFactor;
        this.actuatorType = actuatorType;
        //must final init
        this.metadata = createMetadata();
        this.group = group;
    }

    private Metadata createMetadata() {
        Metadata metadata = new Metadata(wight, pubMode.value, getRpcServerAddress(), hashFactor, actuatorType);
        log.info("@@@ Server start for create Metadata, medata: {}", metadata);
        return metadata;
    }

    /**
     * {@link RPCServerAddress}
     * @return rpc server address.
     */
    protected abstract RPCServerAddress getRpcServerAddress();

    @Override
    public RPCModel getRpcModel() {
        if (rpcModel == null) {
            initRpcContext();
        }
        return rpcModel;
    }

    private synchronized void initRpcContext() throws RpcException {
        try {
            RegistryInfo registryInfo = buildRegistryInfo();
            rpcModel = new RPCModel(application, serverPort, group, registryInfo, getRpcServerAddress(), metadata.toMetadataMap());
        } catch (Throwable cause) {
            throw new RpcException(RpcException.REGISTRY_EXCEPTION, "Failed execute to init rpc context, metadata " + metadata);
        }

    }

    public Metadata getMetadata() {
        return metadata;
    }

    @Override
    public void registerProjectContextInfo() throws RpcException {
        try {
            RPCModel rpcModel = getRpcModel();
            ProjectContextInfo projectContextInfo = SpringContextHolder.getProjectContextInfo();
            if (StringUtils.isBlank(projectContextInfo.getNameEn())) {
                RPCServerAddress serverAddress = rpcModel.getServerAddress();
                UsingIpPort usingIpPort = new UsingIpPort(serverAddress.getHostAddr(), rpcModel.getServerPort(), serverAddress.getPort(), serverAddress.getPid());
                projectContextInfo = new ProjectContextInfo(rpcModel.getName(),
                        EnvironmentConfig.getInstance().getEnvironment(), rpcModel.getPubMode(), usingIpPort , actuatorType);
                //register project context info.
                SpringContextHolder.registerContextInfo(projectContextInfo);
                log.info("Register ProjectContextInfo success, {}.", JsonUtil.toJson(projectContextInfo));
            }
            ProjectContextInfo.setBean(rpcModel);
        } catch (Throwable cause) {
            throw new RpcException(RpcException.REGISTRY_EXCEPTION, "Failed execute registry ProjectContextInfo, cause " + cause.getMessage(), cause);
        }

    }

    private PubMode initPubMode() {
        if (EnvironmentConfig.getInstance().isTestEnvironment()) {
            CommonSwitcher.ENABLE_GRAY_MECHANISM.setStatus(true);
            return PubMode.WHITE;
        } else if (EnvironmentConfig.getInstance().isDevEnvironment() || EnvironmentConfig.getInstance().isUatEnvironment()) {
            CommonSwitcher.ENABLE_GRAY_MECHANISM.setStatus(false);
            return PubMode.GRAY;
        } else {
            CommonSwitcher.ENABLE_GRAY_MECHANISM.setStatus(true);
            return PubMode.WHITE;
        }
    }



    private RegistryInfo buildRegistryInfo() {
        RegistryInfo registryInfo = null;
        try {
            registryInfo = buildRegistryInfo(serverAddress);
        } catch (Throwable cause) {
            log.error("Failed execute obtain NacosDiscoveryProperties from Spring context, cause {}", cause.getMessage());
        }
        if (registryInfo == null) {
            registryInfo = buildRegistryInfo(NacosConfigurationUtils.getServerAddress());
        }
        return registryInfo;

    }


    public static RegistryInfo buildRegistryInfo(String serverAddr) {
        try {
            String hostAddr;
            int port;
            String[] hostAndPort = serverAddr.split(StringConstants.Symbol.COLON);
            if (hostAndPort.length == 1) {
                //if not ip string. try to analysis host.
                hostAddr = getIpByHost(serverAddr);
                port = 0;
            } else {
                port = Integer.parseInt(hostAndPort[1]);
                hostAddr = hostAndPort[0];
                if (!IpUtil.isIP(hostAddr)) {
                    //if not ip string. try to analysis host.
                    hostAddr = getIpByHost(hostAddr);
                }
            }
            RegistryInfo registryInfo = new RegistryInfo(hostAddr, port, serverAddr);
            log.info("Build registry info end, registryInfo = {}", registryInfo);
            return registryInfo;
        } catch (Throwable cause) {
            log.error("Failed execute to buildConnectionInfo, cause {}, serverAddr {}", cause.getMessage(), serverAddr, cause);
            throw cause;
        }
    }

    protected static String getIpByHost(String serverAddr) {
        String ip = NetUtil.getIpByHost(serverAddr);
        AssertUtil.isTrue(StringUtils.isNotBlank(ip) && IpUtil.isIP(ip), "Invalid input serverAddr, addr: " + serverAddr);
        return ip;
    }

}
