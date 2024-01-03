package com.hqy.cloud.registry.deploy;

import com.hqy.cloud.registry.common.context.BeanRepository;
import com.hqy.cloud.registry.common.context.Lifecycle;
import com.hqy.cloud.registry.common.model.DeployModel;
import com.hqy.cloud.util.AssertUtil;
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
    private LifecycleDeployer deployer;

    public LifecycleDeployModel(Lifecycle lifecycle) {
        super(lifecycle.getModel());
        this.lifecycle = lifecycle;
    }

    @Override
    public void initialize() {
        super.initialize();
        this.deployer = BeanRepository.getInstance().getBean(LifecycleDeployer.class);
        AssertUtil.notNull(this.deployer, "Deploy not register to repository.");
        // init lifecycle
        getLifecycle().initialize();
    }

    public Lifecycle getLifecycle() {
        return lifecycle;
    }

    @Override
    public void onDestroy() {
        log.info("On destroy lifecycle deployer.");

        // pre-destroy, set stopping
        if (deployer != null) {
            deployer.preDestroy();
        }
        // destroy lifecycle.
        lifecycle.destroy();

        // post-destroy, set stopping
        if (deployer != null) {
            deployer.postDestroy();
        }

    }

}
