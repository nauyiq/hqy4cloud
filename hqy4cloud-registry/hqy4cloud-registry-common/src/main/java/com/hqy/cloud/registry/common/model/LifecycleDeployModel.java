package com.hqy.cloud.registry.common.model;

import com.hqy.cloud.registry.common.context.Lifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LifecycleDeployModel
 * @see com.hqy.cloud.registry.common.context.Lifecycle
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/29 13:47
 */
public class LifecycleDeployModel extends DeployModel {
    private final static Logger log = LoggerFactory.getLogger(LifecycleDeployModel.class);
    private final Lifecycle lifecycle;
    private static final String NAME = "LifeCycleModel";

    public LifecycleDeployModel(ApplicationModel model, Lifecycle lifecycle) {
        super(model);
        this.lifecycle = lifecycle;
    }

    protected void doInit() {
        lifecycle.initialize();
    }

    public Lifecycle getLifecycle() {
        return lifecycle;
    }

    @Override
    public void onDestroy() {
        if (!lifecycle.isAvailable()) {
            log.info("The lifecycle already destroy.");
            return;
        }
        lifecycle.destroy();
    }

    @Override
    public String modelName() {
        return NAME;
    }
}
