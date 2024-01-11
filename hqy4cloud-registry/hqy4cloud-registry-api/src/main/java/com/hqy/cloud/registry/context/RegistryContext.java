package com.hqy.cloud.registry.context;

import com.hqy.cloud.registry.api.Registry;
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

import java.util.Comparator;
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
    private final Map<String, DeployModel> childDeployModels = new ConcurrentHashMap<>();


    public RegistryContext(ApplicationModel model, Registry registry, MasterElectionService masterElectionService) {
        this.model = model;
        this.registry = registry;
        this.masterElectionService = masterElectionService;
    }

    public void addDeployModel(DeployModel deployModel) {
        AssertUtil.notNull(deployModel, "Deploy model should not be null.");
        childDeployModels.put(deployModel.getModelName(), deployModel);
    }

    @Override
    public boolean isAvailable() {
        return model.isHealthy();
    }

    @Override
    public void destroy() {
        try {
            this.registry.destroy();
            if (!this.childDeployModels.isEmpty()) {
                childDeployModels.values().forEach(DeployModel::destroy);
            }
        } catch (Exception e) {
            log.error("Failed execute to destroy Registry lifecycle, cause: {}", e.getMessage(), e);
        }
    }

    @Override
    public void initialize() {
        List<DeployModel> models = childDeployModels.values().stream().sorted(Comparator.comparingInt(s -> s.getMetaDataClaim().getPriority())).toList();
        // init all models
        if (!this.childDeployModels.isEmpty()) {
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
        registry.subscribe(registry.getInstance(), new ClusterServiceNotifyListener(masterElectionService));
        // start models
        childDeployModels.values().forEach(DeployModel::start);
    }

    @Override
    public ApplicationModel getModel() {
        return this.model;
    }
}
