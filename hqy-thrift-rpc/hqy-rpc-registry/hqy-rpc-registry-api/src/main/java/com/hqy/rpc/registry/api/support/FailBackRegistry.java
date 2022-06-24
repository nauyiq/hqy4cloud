package com.hqy.rpc.registry.api.support;

import com.hqy.rpc.common.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Failure automatically restores the registry
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/24 17:26
 */
public abstract class FailBackRegistry extends AbstractRegistry {

    private static final Logger log = LoggerFactory.getLogger(FailBackRegistry.class);

    public FailBackRegistry(URL url) {
        super(url);
    }






}
