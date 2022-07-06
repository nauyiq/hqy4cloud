package com.hqy.rpc.client.thrift;

import com.hqy.rpc.cluster.directory.DynamicDirectory;
import com.hqy.rpc.common.Metadata;
import com.hqy.rpc.registry.api.Registry;
import com.hqy.rpc.registry.api.RegistryFactory;
import com.hqy.rpc.registry.nacos.NacosRegistryFactory;
import com.hqy.util.AssertUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/4 17:53
 */
public class ThriftDynamicDirectory<T> extends DynamicDirectory<T> {

    private final RegistryFactory factory;

    private static final Logger log = LoggerFactory.getLogger(ThriftDynamicDirectory.class);

    public ThriftDynamicDirectory(String providerServiceName, Metadata consumerMetadata, Class<T> serviceType) {
        super(providerServiceName, consumerMetadata, serviceType);
        factory = new NacosRegistryFactory();
    }

    @Override
    public Registry setRegistry() {
        return factory.getRegistry(consumerMetadata());
    }

    @Override
    public void notify(List<Metadata> metadataList) {
        if (isDestroyed()) {
            return;
        }
        metadataList = metadataList.stream().filter(Objects::nonNull).collect(Collectors.toList());
        refreshOverrideAndInvoker(metadataList);
    }

    private synchronized void refreshOverrideAndInvoker(List<Metadata> metadataList) {
        refreshInvoker(metadataList);
    }

    private void refreshInvoker(List<Metadata> invokerMetadataList) {
        AssertUtil.notNull(invokerMetadataList, "invokerMetadataList should not be null.");

        if (CollectionUtils.isEmpty(invokerMetadataList)) {
            this.forbidden = true;
            routerChain.setInvokers(Collections.emptyList());
            destroyInvokers();
        } else {
            this.forbidden = false;
//            List<> toInvokers(invokerMetadataList);
        }
    }


    @Override
    protected void destroyAllInvokers() {

    }

}
