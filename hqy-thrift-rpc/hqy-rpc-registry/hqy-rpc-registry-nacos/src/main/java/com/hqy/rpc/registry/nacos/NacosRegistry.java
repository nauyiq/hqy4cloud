package com.hqy.rpc.registry.nacos;

import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.hqy.base.common.base.lang.exception.RpcException;
import com.hqy.rpc.registry.api.NotifyListener;
import com.hqy.rpc.registry.api.Registry;
import com.hqy.rpc.registry.api.support.FailBackRegistry;
import com.hqy.rpc.registry.nacos.naming.NamingServiceWrapper;
import com.hqy.rpc.common.Metadata;
import com.hqy.util.AssertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.hqy.rpc.common.CommonConstants.*;

/**
 * Nacos {@link Registry}
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/23 17:19
 */
public class NacosRegistry extends FailBackRegistry {

    private static final Logger log = LoggerFactory.getLogger(NacosRegistry.class);
    private static final String UP = "UP";

    private final NamingServiceWrapper namingService;

    public NacosRegistry(Metadata metadata, NamingServiceWrapper namingService) {
        super(metadata);
        this.namingService = namingService;
    }

    @Override
    public List<Metadata> lookup(Metadata metadata) {
        return null;
    }

    @Override
    public void doRegister(Metadata metadata) {
        AssertUtil.notNull(metadata, "Nacos Registry register failed, metadata is null.");
        try {
            String serviceName = metadata.getServiceName();
            Instance instance = createInstance(metadata);
            namingService.registerInstance(serviceName, getGroup(metadata), instance);
        } catch (Throwable cause) {
            throw new RpcException("Failed to register to nacos " + getMetadata() + ", cause: " + cause.getMessage(), cause);
        }
    }

    @Override
    public void doUnregister(Metadata metadata) {
        AssertUtil.notNull(metadata, "Nacos Registry unregister failed, metadata is null.");
        try {
            String serviceName = metadata.getServiceName();
            Instance instance = createInstance(metadata);
            namingService.deregisterInstance(serviceName, getGroup(metadata), instance);
        } catch (Throwable cause) {
            throw new RpcException("Failed to unregister to nacos " + getMetadata() + ", cause: " + cause.getMessage(), cause);
        }
    }

    @Override
    public void doSubscribe(Metadata metadata, NotifyListener listener) {

    }

    @Override
    public void doUnsubscribe(Metadata metadata, NotifyListener listener) {

    }



    @Override
    public boolean isAvailable() {
        return UP.equals(namingService.getServerStatus());
    }

    private Instance createInstance(Metadata metadata) {
        Instance instance = new Instance();
        instance.setIp(metadata.getHost());
        instance.setPort(metadata.getPort());
        instance.setMetadata(NacosMetadataContext.buildMetadata(metadata.getNode()));
        return instance;
    }

    private String getGroup(Metadata metadata) {
        return metadata.getParameter(GROUP_KEY, Constants.DEFAULT_GROUP);
    }







}
