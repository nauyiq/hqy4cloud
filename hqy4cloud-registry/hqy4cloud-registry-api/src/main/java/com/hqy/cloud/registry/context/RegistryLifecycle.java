package com.hqy.cloud.registry.context;

import com.hqy.cloud.registry.api.Registry;
import com.hqy.cloud.registry.cluster.MasterElectionService;
import com.hqy.cloud.registry.cluster.support.ClusterServiceNotifyListener;
import com.hqy.cloud.registry.common.context.Lifecycle;
import com.hqy.cloud.registry.common.model.ApplicationModel;
import com.hqy.cloud.registry.common.model.DeployModel;
import com.hqy.cloud.util.AssertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RegistryLifecycle.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/2
 */
public class RegistryLifecycle implements Lifecycle {
    private static final Logger log = LoggerFactory.getLogger(RegistryLifecycle.class);

    private final ApplicationModel model;
    private final Registry registry;
    private final MasterElectionService masterElectionService;
    private final Map<String, DeployModel> models = new ConcurrentHashMap<>();

    public RegistryLifecycle(ApplicationModel model, Registry registry, MasterElectionService masterElectionService) {
        this.model = model;
        this.registry = registry;
        this.masterElectionService = masterElectionService;
    }

    public void addDeployModel(DeployModel deployModel) {
        AssertUtil.notNull(deployModel, "Deploy model should not be null.");
        models.put(deployModel.getModelName(), deployModel);
    }

    @Override
    public boolean isAvailable() {
        return model.isHealthy();
    }

    @Override
    public void destroy() {
        try {
            this.registry.destroy();
            if (!this.models.isEmpty()) {
                models.values().forEach(DeployModel::destroy);
            }
        } catch (Exception e) {
            log.error("Failed execute to destroy Registry lifecycle, cause: {}", e.getMessage(), e);
        }
    }

    @Override
    public void initialize() {
        // init all models
        if (!this.models.isEmpty()) {
            models.values().forEach(DeployModel::initialize);
        }
    }

    @Override
    public void start() {
        // register master listener.
        registry.subscribe(registry.getInstance(), new ClusterServiceNotifyListener(masterElectionService));
    }

    @Override
    public ApplicationModel getModel() {
        return this.model;
    }
}
