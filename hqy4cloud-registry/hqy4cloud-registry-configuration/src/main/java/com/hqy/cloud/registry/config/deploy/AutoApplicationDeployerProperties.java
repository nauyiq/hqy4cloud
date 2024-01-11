package com.hqy.cloud.registry.config.deploy;

import com.hqy.cloud.common.base.lang.ActuatorNode;
import com.hqy.cloud.registry.common.Constants;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.hqy.cloud.registry.config.deploy.AutoApplicationDeployerProperties.PREFIX;


/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/8
 */
@ConfigurationProperties(PREFIX)
public class AutoApplicationDeployerProperties {
    public static final String PREFIX = "hqy4cloud.application.deploy";

    private boolean enabled = true;

    private ActuatorNode actuatorType = ActuatorNode.CONSUMER;

    private String revision = Constants.DEFAULT_REVISION;

    private int weight =  Constants.DEFAULT_WEIGHT;


    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ActuatorNode getActuatorType() {
        return actuatorType;
    }

    public void setActuatorType(ActuatorNode actuatorType) {
        this.actuatorType = actuatorType;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
