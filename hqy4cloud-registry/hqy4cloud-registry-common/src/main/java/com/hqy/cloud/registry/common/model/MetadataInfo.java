package com.hqy.cloud.registry.common.model;

import com.hqy.cloud.common.base.Parameters;
import com.hqy.cloud.common.base.lang.ActuatorNode;
import com.hqy.cloud.registry.common.model.RegistryInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Metadata of the service instance registered to the registry.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/29
 */
@Slf4j
public class MetadataInfo extends Parameters {

    private String application;
    private String env;
    private ActuatorNode actuatorNode;
    private String revision;
    private boolean master;

    private ConcurrentHashMap<String, SortedSet<RegistryInfo>> subscribeServiceInfos;


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
}
