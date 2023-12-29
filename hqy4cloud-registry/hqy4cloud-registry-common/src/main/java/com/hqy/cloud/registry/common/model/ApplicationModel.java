package com.hqy.cloud.registry.common.model;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.common.base.Parameters;
import com.hqy.cloud.common.base.lang.StringConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The service instance model.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/29 10:02
 */
public class ApplicationModel extends Parameters implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(ApplicationModel.class);

    private String applicationName;
    private String namespace;
    private String group;
    private Boolean healthy;
    private int port;
    private String ip;
    private RegistryInfo registryInfo;
    private MetadataInfo metadataInfo;
    private Long startupTimeMillis;
    private Map<String, DeployModel> deployModels = new ConcurrentHashMap<>();

    /**
     * cache.
     */
    private volatile transient Map<String, Number> numbers;

    public String getApplicationName() {
        return applicationName;
    }

    public String getApplicationDesc() {
        return getApplicationName() + " register to " + getRegistryInfo().getName()
                + StringConstants.Symbol.COLON + buildString(false);
    }

    public Map<String, Number> getNumbers() {
        if (numbers == null) {
            numbers = MapUtil.newConcurrentHashMap();
        }
        return numbers;
    }

    public int getParameter(String key, int defaultValue) {
        Number number = getNumbers().get(key);
        if (number != null) {
            return number.intValue();
        }
        String value  = getParameter(key);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        int intValue = Integer.parseInt(value);
        getNumbers().put(key, intValue);
        return intValue;
    }

    private String buildString(boolean appendUser) {
        StringBuilder buf = new StringBuilder();
        if (appendUser && StringUtils.isNotEmpty(getUsername())) {
            buf.append(getUsername());
            if (StringUtils.isNotEmpty(getPassword())) {
                buf.append(':');
                buf.append(getPassword());
            }
            buf.append(StringConstants.Symbol.AT);
        }

        String host = getHost();
        if (StringUtils.isNotEmpty(host)) {
            buf.append(host);
            buf.append(StringConstants.Symbol.AT);
        }
        buf.append(getApplicationName());
        return buf.toString();
    }

    public Map<String, DeployModel> getDeployModels() {
        return deployModels;
    }

    public void setDeployModels(Map<String, DeployModel> deployModels) {
        this.deployModels = deployModels;
    }

    public RegistryInfo getRegistryInfo() {
        return registryInfo;
    }

    public String getUsername() {
        return registryInfo == null ? null : registryInfo.getUsername();
    }

    public String getPassword() {
        return registryInfo == null ? null : registryInfo.getPassword();
    }

    public String getHost() {
        String host = ip == null ? null : ip;
        if (StringUtils.isNotBlank(host)) {
            host = host + StringConstants.Symbol.COLON + getPort();
        }
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getGroup() {
        return group;
    }

    public Boolean isHealthy() {
        return healthy;
    }

    public String getIp() {
        return ip;
    }

    public MetadataInfo getMetadataInfo() {
        return metadataInfo;
    }

    public Long getStartupTimeMillis() {
        return startupTimeMillis;
    }
}
