package com.hqy.cloud.registry.context;

import com.hqy.cloud.registry.api.Registry;
import com.hqy.cloud.registry.api.support.ApplicationServiceInstance;
import com.hqy.cloud.registry.cluster.MasterElectionService;
import com.hqy.cloud.registry.cluster.support.ClusterServiceNotifyListener;
import com.hqy.cloud.registry.common.context.Lifecycle;
import com.hqy.cloud.registry.common.metadata.MetadataInfo;
import com.hqy.cloud.registry.common.metadata.RegistryMetadataClaim;
import com.hqy.cloud.registry.common.model.ApplicationModel;
import com.hqy.cloud.registry.common.model.DeployModel;
import com.hqy.cloud.util.AssertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RegistryContext.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/2
 */
public class RegistryContext implements Lifecycle {
    private static final Logger log = LoggerFactory.getLogger(RegistryContext.class);

    private final ApplicationModel model;
    private final Registry registry;
    private final MasterElectionService masterElectionService;
    private static final Map<String, DeployModel> CHILD_DEPLOY_MODELS = new ConcurrentHashMap<>();


    public RegistryContext(ApplicationModel model, Registry registry, MasterElectionService masterElectionService) {
        this.model = model;
        this.registry = registry;
        this.masterElectionService = masterElectionService;
    }

    public static void addDeployModel(DeployModel deployModel) {
        AssertUtil.notNull(deployModel, "Deploy model should not be null.");
        CHILD_DEPLOY_MODELS.put(deployModel.getModelName(), deployModel);
    }

    @Override
    public boolean isAvailable() {
        return model.isHealthy();
    }

    @Override
    public void destroy() {
        try {
            this.registry.destroy();
            if (!CHILD_DEPLOY_MODELS.isEmpty()) {
                CHILD_DEPLOY_MODELS.values().forEach(DeployModel::destroy);
            }
        } catch (Exception e) {
            log.error("Failed execute to destroy Registry lifecycle, cause: {}", e.getMessage(), e);
        }
    }

    @Override
    public void initialize() {
        List<DeployModel> models = new ArrayList<>(CHILD_DEPLOY_MODELS.values());
        Collections.sort(models);
        // init all models
        if (!CHILD_DEPLOY_MODELS.isEmpty()) {
            models.forEach(DeployModel::initialize);
        }
        // init all models metadata
        MetadataInfo metadataInfo = this.model.getMetadataInfo();
        for (DeployModel deployModel : models) {
            RegistryMetadataClaim claim = deployModel.getMetaDataClaim();
            metadataInfo = claim.claim(metadataInfo, deployModel.getMetadataMap());
        }
        this.model.setMetadataInfo(metadataInfo);
    }

    @Override
    public void start() {
        // register master listener.
        registry.subscribe(new ApplicationServiceInstance(model), new ClusterServiceNotifyListener(masterElectionService));
        // start models
        CHILD_DEPLOY_MODELS.values().forEach(DeployModel::start);
    }

    @Override
    public ApplicationModel getModel() {
        return this.model;
    }
}
