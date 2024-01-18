package com.hqy.cloud.registry.cluster.support;

import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.registry.api.ServiceInstance;
import com.hqy.cloud.registry.api.ServiceNotifyListener;
import com.hqy.cloud.registry.cluster.MasterElectionService;
import com.hqy.cloud.registry.common.context.BeanRepository;
import com.hqy.cloud.registry.common.deploy.DeployState;
import com.hqy.cloud.registry.context.ProjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * choose cluster instance listener.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/2
 */
public record ClusterServiceNotifyListener(MasterElectionService electionService) implements ServiceNotifyListener {
    private static final Logger log = LoggerFactory.getLogger(ClusterServiceNotifyListener.class);

    @Override
    public void notify(List<ServiceInstance> instances) {
        if (CommonSwitcher.ENABLE_REGISTRY_MASTER_NODE_LISTENER.isOff()) {
            return;
        }
        ProjectContext context = BeanRepository.getInstance().getBean(ProjectContext.class);
        if (context != null) {
            DeployState state = context.deployer().getState();
            if (state != DeployState.STARTED) {
                // just not started, ignore event.
                log.info("Ignore cluster notify, deploy state {}.", state);
                return;
            }
        }
        electionService.elect(instances);
    }



}
