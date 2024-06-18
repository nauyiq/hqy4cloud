package com.hqy.cloud.registry.common.model;

import com.hqy.cloud.common.base.Parameters;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.registry.common.metadata.MetadataInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * The service instance model.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/29
 */
public class ProjectInfoModel extends Parameters implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(ProjectInfoModel.class);

    private String id;
    private String applicationName;
    private String namespace;
    private String group;
    private Boolean healthy;
    private int port;
    private String ip;
    private RegistryInfo registryInfo;
    private MetadataInfo metadataInfo;
    private Long startupTimeMillis;

    private ProjectInfoModel(String applicationName, String namespace, String group) {
        this.applicationName = applicationName;
        this.namespace = namespace;
        this.group = group;
    }

    public static ProjectInfoModel of(String applicationName, String namespace, String group) {
        return new ProjectInfoModel(applicationName, namespace, group);
    }

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


    public double getParameter(String key, double defaultValue) {
        Number number = getNumbers().get(key);
        if (number != null) {
            return number.doubleValue();
        }
        String value  = getParameter(key);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        double doubleValue = Double.parseDouble(value);
        getNumbers().put(key, doubleValue);
        return doubleValue;
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

    public Map<String, String> getMetadataMap() {
        return this.metadataInfo.getParameters();
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setHealthy(Boolean healthy) {
        this.healthy = healthy;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setRegistryInfo(RegistryInfo registryInfo) {
        this.registryInfo = registryInfo;
    }

    public void setMetadataInfo(MetadataInfo metadataInfo) {
        this.metadataInfo = metadataInfo;
    }

    public void setStartupTimeMillis(Long startupTimeMillis) {
        this.startupTimeMillis = startupTimeMillis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectInfoModel that = (ProjectInfoModel) o;
        return port == that.port && applicationName.equals(that.applicationName) && namespace.equals(that.namespace) && group.equals(that.group) && ip.equals(that.ip) && registryInfo.equals(that.registryInfo) && Objects.equals(metadataInfo, that.metadataInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(applicationName, namespace, group, port, ip, registryInfo);
    }
}
