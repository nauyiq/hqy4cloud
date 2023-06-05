package com.hqy.cloud.rpc.model;

import com.hqy.cloud.common.base.lang.ActuatorNode;
import com.hqy.cloud.rpc.core.Environment;
import com.hqy.cloud.rpc.deploy.ApplicationDeployer;
import com.hqy.cloud.util.spring.ProjectContextInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 表示当前应用上下文，储存了了基本数据信息使用
 * 可以在RPC调用过程中获取服务提供者的模型和订阅服务的消费者模型
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/31 17:53
 */
public class ApplicationModel extends ScopeModel {
    private static final Logger log = LoggerFactory.getLogger(ApplicationModel.class);
    private ModuleModel model;
    private ModuleModel childModel;
    private ApplicationDeployer deployer;
    private Environment environment;
    private PubMode pubMode;
    private ActuatorNode actuatorNode;

    private ApplicationModel(RPCModel rpcModel) {
        super(rpcModel);
    }

    public static ApplicationModel of(RPCModel rpcModel, ModuleModel model) {
        ApplicationModel applicationModel = new ApplicationModel(rpcModel);
        applicationModel.setModel(model);
        return applicationModel;
    }

    public static ApplicationModel of(RPCModel rpcModel, ModuleModel model, ModuleModel childModel) {
        ApplicationModel applicationModel = new ApplicationModel(rpcModel);
        applicationModel.setModel(model);
        applicationModel.setChildModel(childModel);
        return applicationModel;
    }

    @Override
    public void initialize() {
        super.initialize();
        this.environment = Environment.getInstance();
        this.pubMode = PubMode.of(getSelfModel().getPubMode());
        this.deployer = ProjectContextInfo.getBean(ApplicationDeployer.class);
        if (deployer != null) {
            if (this.model != null) {
                this.model.initialize(this.deployer.getExecutorRepository());
            }
            if (this.childModel != null) {
                this.childModel.initialize(this.deployer.getExecutorRepository());
            }
        }
    }

    @Override
    public void onDestroy() {
        log.info("On destroy application deployer.");

        // pre-destroy, set stopping
        if (deployer != null) {
            deployer.preDestroy();
        }

        // destroy model.
        if (model != null && !model.isDestroy()) {
            model.destroy();
        }
        if (childModel != null && !childModel.isDestroy()) {
            childModel.destroy();
        }

        // post-destroy, set stopping
        if (deployer != null) {
            deployer.postDestroy();
        }
    }

    public ActuatorNode getActuatorNode() {
        return actuatorNode;
    }

    public void setActuatorNode(ActuatorNode actuatorNode) {
        this.actuatorNode = actuatorNode;
    }

    public void setChildModel(ModuleModel childModel) {
        this.childModel = childModel;
    }

    public Environment getEnvironment() {
        return this.environment;
    }

    public ApplicationDeployer getDeployer() {
        return deployer;
    }

    public void setDeployer(ApplicationDeployer deployer) {
        this.deployer = deployer;
    }

    public PubMode getPubMode() {
        return pubMode;
    }

    public void setModel(ModuleModel model) {
        this.model = model;
    }
}
