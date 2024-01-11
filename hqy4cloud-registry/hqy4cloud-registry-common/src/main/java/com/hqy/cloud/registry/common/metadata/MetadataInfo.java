package com.hqy.cloud.registry.common.metadata;

import com.hqy.cloud.common.base.Parameters;
import com.hqy.cloud.common.base.lang.ActuatorNode;
import com.hqy.cloud.registry.common.deploy.DeployMetaDataService;
import com.hqy.cloud.registry.common.model.PubMode;
import com.hqy.cloud.registry.common.model.RegistryInfo;

import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Metadata of the service instance registered to the registry.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/29
 */
public class MetadataInfo extends Parameters implements DeployMetaDataService {

    private String application;
    private String env;
    private ActuatorNode actuatorNode;
    private PubMode pubMode;
    private String revision;
    private boolean master;
    private int weight;

    public MetadataInfo(String application) {
        this.application = application;
    }

    public MetadataInfo(String application, String env, ActuatorNode actuatorNode, PubMode pubMode, String revision, boolean master) {
        this.application = application;
        this.env = env;
        this.actuatorNode = actuatorNode;
        this.pubMode = pubMode;
        this.revision = revision;
        this.master = master;
    }

    private ConcurrentHashMap<String, SortedSet<RegistryInfo>> subscribeServiceInfos;

    @Override
    public Map<String, String> getMetadataMap() {
        return getParameters();
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public ActuatorNode getActuatorNode() {
        return actuatorNode;
    }

    public void setActuatorNode(ActuatorNode actuatorNode) {
        this.actuatorNode = actuatorNode;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public boolean isMaster() {
        return master;
    }

    public void setMaster(boolean master) {
        this.master = master;
    }

    public ConcurrentHashMap<String, SortedSet<RegistryInfo>> getSubscribeServiceInfos() {
        return subscribeServiceInfos;
    }

    public void setSubscribeServiceInfos(ConcurrentHashMap<String, SortedSet<RegistryInfo>> subscribeServiceInfos) {
        this.subscribeServiceInfos = subscribeServiceInfos;
    }

    public PubMode getPubMode() {
        return pubMode;
    }

    public void setPubMode(PubMode pubMode) {
        this.pubMode = pubMode;
    }
}
