package com.hqy.cloud.registry.deploy;

import com.hqy.cloud.registry.common.context.BeanRepository;
import com.hqy.cloud.registry.common.model.DeployModel;
import com.hqy.cloud.registry.context.RegistryContext;
import com.hqy.cloud.util.AssertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ApplicationLifecycleDeployModel
 * @see com.hqy.cloud.registry.common.context.Lifecycle
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/29 13:47
 */
public class ApplicationDeployModel extends DeployModel {
    private final static Logger log = LoggerFactory.getLogger(ApplicationDeployModel.class);

    private final RegistryContext context;
    private ApplicationLifecycleDeployer deployer;

    public ApplicationDeployModel(RegistryContext context) {
        super(context.getModel());
        this.context = context;
    }

    @Override
    public void initialize() {
        super.initialize();
        this.deployer = BeanRepository.getInstance().getBean(ApplicationLifecycleDeployer.class);
        AssertUtil.notNull(this.deployer, "Deploy not register to repository.");
        // init lifecycle
        getRegistryContext().initialize();
    }

    public RegistryContext getRegistryContext() {
        return this.context;
    }

    @Override
    public void onDestroy() {
        log.info("On destroy lifecycle deployer.");

        // pre-destroy, set stopping
        if (deployer != null) {
            deployer.preDestroy();
        }
        // destroy lifecycle.
        getRegistryContext().destroy();

        // post-destroy, set stopping
        if (deployer != null) {
            deployer.postDestroy();
        }
    }
}
