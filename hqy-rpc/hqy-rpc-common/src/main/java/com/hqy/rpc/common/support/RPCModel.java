package com.hqy.rpc.common.support;

import cn.hutool.core.map.MapUtil;
import com.hqy.base.common.support.Parameters;
import com.hqy.base.common.swticher.CommonSwitcher;
import com.hqy.rpc.common.RPCServerAddress;
import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.rpc.common.CommonConstants;
import com.hqy.rpc.common.PubMode;
import com.hqy.rpc.common.transaction.TransactionContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * RPCModel- Uniform Resource Locator
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/7 17:17
 */
public class RPCModel extends Parameters implements Serializable {
    private transient static final long serialVersionUID = -1724139538145932293L;

    private static final Logger log = LoggerFactory.getLogger(RPCModel.class);

    /**
     * server name. general custom for ${spring.application.name}
     */
    private final String name;

    /**
     * server port, not rpc port.
     */
    private final int serverPort;

    /**
     * group.
     */
    private final String group;

    /**
     * registry information.
     */
    private final RegistryInfo registryInfo;

    /**
     * rpc server address.
     */
    private final RPCServerAddress serverAddress;

    /**
     * this context create timestamp
     */
    private final long createTimestamp;

    /**
     * cache.
     */
    private volatile transient Map<String, Number> numbers;

    public RPCModel(String name) {
        this(name, 0);
    }

    public RPCModel(String name, int serverPort) {
        this(name, serverPort, CommonConstants.DEFAULT_GROUP);
    }

    public RPCModel(String name, int serverPort, String group) {
        this(name, serverPort, group, null, null);
    }

    public RPCModel(String name, int serverPort, String group, RegistryInfo registryInfo, RPCServerAddress address) {
        this(name, serverPort, group, registryInfo, address, MapUtil.newConcurrentHashMap());
    }

    public RPCModel(String name, int serverPort, String group, RegistryInfo registryInfo, RPCServerAddress serverAddress, Map<String, String> parameters) {
        this.name = name;
        this.serverPort = serverPort;
        this.group = group;
        this.registryInfo = registryInfo;
        this.serverAddress = serverAddress;
        this.parameters = parameters;
        this.createTimestamp = System.currentTimeMillis();
    }

    public static RPCModel setApplication(String application) {
        return new RPCModel(application);
    }


    public int getServerPort() {
        return serverPort;
    }

    public String getConnectionParam(String key) {
        return registryInfo.getParameter(key);
    }

    public String getConnectionParam(String key, String defaultValue) {
        return registryInfo.getParameter(key, defaultValue);
    }

    public Map<String, String> getConnectionParams() {
        return registryInfo.getConnectionParams();
    }

    public String toServiceString() {
        return buildString(false, true, true);
    }

    private String buildString(boolean appendUser, boolean useRawAddr, boolean useServiceName) {
        StringBuilder buf = new StringBuilder();
        if (appendUser && StringUtils.isNotEmpty(getUsername())) {
            buf.append(getUsername());
            if (StringUtils.isNotEmpty(getPassword())) {
                buf.append(':');
                buf.append(getPassword());
            }
            buf.append(StringConstants.Symbol.AT);
        }
        String host;
        if (useRawAddr) {
            host = getHost();
        } else {
            host = serverAddress.getHostAddr();
        }

        if (StringUtils.isNotEmpty(host)) {
            buf.append(host);
            buf.append(StringConstants.Symbol.AT);
        }

        if (useServiceName) {
            buf.append(getName());
        }

        return buf.toString();
    }

    public RPCServerAddress getServerAddress() {
        return serverAddress;
    }

    public String getRegistryAddress() {
        return registryInfo.getAddress();
    }

    public RegistryInfo getRegistryInfo() {
        return registryInfo;
    }

    public String getName() {
        return name;
    }

    public String getHost() {
        String host = serverAddress == null ? null : serverAddress.getHostAddr();
        if (StringUtils.isNotBlank(host)) {
            host = host + StringConstants.Symbol.COLON + getPort();
        }
        return host;
    }

    public int getPort() {
        return serverAddress == null ? 0 : serverAddress.getPort();
    }

    public String getUsername() {
        return registryInfo == null ? null : registryInfo.getUsername();
    }

    public String getPassword() {
        return registryInfo == null ? null : registryInfo.getPassword();
    }

    public Map<String, Number> getNumbers() {
        if (numbers == null) {
            numbers = MapUtil.newConcurrentHashMap();
        }
        return numbers;
    }

    public String getGroup() {
        return this.group;
    }

    public String getHashFactor() {
        return getParameter(CommonConstants.HASH_FACTOR, CommonConstants.DEFAULT_HASH_FACTOR);
    }

    public void setHashFactor(String hashFactor) {
        setParameter(CommonConstants.HASH_FACTOR, StringUtils.isBlank(hashFactor) ? CommonConstants.DEFAULT_HASH_FACTOR : hashFactor);
    }

    public long getCreateTimestamp() {
        return createTimestamp;
    }

    public int getWeight() {
        return getParameter(CommonConstants.WEIGHT, CommonConstants.DEFAULT_WEIGHT);
    }

    public int getPubMode() {
        return getParameter(CommonConstants.PUB_MODE, PubMode.GRAY.value);
    }

    public long serverStartTimestamp() {
        return getParameter(CommonConstants.START_SERVER_TIMESTAMP, getCreateTimestamp());
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

    public long getParameter(String key, long defaultValue) {
        Number number = getNumbers().get(key);
        if (number != null) {
            return number.longValue();
        }
        String value  = getParameter(key);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        long longValue = Long.parseLong(value);
        getNumbers().put(key, longValue);
        return longValue;
    }

    public Boolean getParameter(String key, Boolean defaultValue) {
        String value = getParameter(key);
        if (StringUtils.isBlank(key)) {
            return defaultValue;
        }
        try {
            return Boolean.parseBoolean(value);
        } catch (Throwable t) {
            log.warn("Get boolean parameter happen error, key {}. cause {}", key, t.getMessage(), t);
            return defaultValue;
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("serverPort", serverPort)
                .append("registryInfo", registryInfo)
                .append("serverAddress", serverAddress)
                .append("createTimestamp", createTimestamp)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RPCModel that = (RPCModel) o;
        return Objects.equals(name, that.name) && Objects.equals(registryInfo, that.registryInfo) && Objects.equals(serverAddress, that.serverAddress) && this.serverPort == that.serverPort;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, serverPort, registryInfo, serverAddress);
    }



}
