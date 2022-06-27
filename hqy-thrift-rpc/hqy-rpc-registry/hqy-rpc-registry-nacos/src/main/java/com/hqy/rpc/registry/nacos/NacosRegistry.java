package com.hqy.rpc.registry.nacos;

import com.hqy.rpc.common.Metadata;
import com.hqy.rpc.registry.api.NotifyListener;
import com.hqy.rpc.registry.api.Registry;
import com.hqy.rpc.registry.api.support.FailBackRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Nacos {@link Registry}
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/23 17:19
 */
public class NacosRegistry extends FailBackRegistry {

    private static final Logger log = LoggerFactory.getLogger(NacosRegistry.class);



    @Override
    public void doRegister(Metadata metadata) {

    }

    @Override
    public void doUnregister(Metadata metadata) {

    }

    @Override
    public void doSubscribe(Metadata metadata, NotifyListener listener) {

    }

    @Override
    public void doUnsubscribe(Metadata metadata, NotifyListener listener) {

    }

    @Override
    public boolean isAvailable() {
        return false;
    }
}
