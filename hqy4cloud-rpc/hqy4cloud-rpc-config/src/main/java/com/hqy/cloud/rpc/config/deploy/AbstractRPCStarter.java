package com.hqy.cloud.rpc.config.deploy;

import com.hqy.cloud.common.base.lang.exception.RpcException;
import com.hqy.cloud.common.base.project.UsingIpPort;
import com.hqy.cloud.rpc.deploy.ApplicationDeployer;
import com.hqy.cloud.rpc.model.ApplicationModel;
import com.hqy.cloud.rpc.model.RPCModel;
import com.hqy.cloud.rpc.model.RPCServerAddress;
import com.hqy.cloud.util.spring.ProjectContextInfo;
import com.hqy.cloud.util.spring.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

import static com.hqy.cloud.common.base.lang.exception.RpcException.UNKNOWN_EXCEPTION;

/**
 * AbstractRPCStarter.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/2 15:23
 */
public abstract class AbstractRPCStarter implements RPCStarter {
    private final static Logger log = LoggerFactory.getLogger(AbstractRPCStarter.class);
    private final ApplicationDeployer deployer;

    public AbstractRPCStarter(ApplicationModel model) {
        this.deployer = new DefaultApplicationDeployer(model);
        ProjectContextInfo.setBean(ApplicationDeployer.class, deployer);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean start() {
        Future<Boolean> future = (Future<Boolean>) this.deployer.start();
        try {
            if (future.get()) {
                registerProjectContextInfo();
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RpcException(UNKNOWN_EXCEPTION, e);
        }
    }

    public ApplicationDeployer getDeployer() {
        return deployer;
    }

    @Override
    public RPCModel getModel() {
        return deployer.getModel().getSelfModel();
    }

    @Override
    public boolean isAvailable() {
        return deployer.isRunning();
    }

    @Override
    public void destroy() {
        deployer.stop();
    }

    protected void registerProjectContextInfo() throws RpcException {
        RPCModel model = this.getModel();
        ProjectContextInfo contextInfo = SpringContextHolder.getProjectContextInfo();
        if (contextInfo == null) {
            // register projectContextInfo
            ApplicationModel applicationModel = deployer.getModel();
            contextInfo = new ProjectContextInfo(model.getName(), applicationModel.getEnvironment().getEnvironment(),
                    applicationModel.getPubMode().value, buildUsingIpPort(model), applicationModel.getActuatorNode());
            SpringContextHolder.registerContextInfo(contextInfo);
        }

    }

    private UsingIpPort buildUsingIpPort(RPCModel model) {
        RPCServerAddress serverAddress = model.getServerAddress();
        return new UsingIpPort(serverAddress.getHostAddr(),
                model.getServerPort(), serverAddress.getPort(), serverAddress.getPid());
    }
}
