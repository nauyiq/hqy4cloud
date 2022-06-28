package com.hqy.rpc.registry.node;

import com.hqy.base.common.base.lang.StringConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/23 12:17
 */
public class Metadata implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(Metadata.class);

    private static final long serialVersionUID = -2179067844822981338L;

    private final ConnectionInfo connectionInfo;

    private final Node node;

    public Metadata(ConnectionInfo connectionInfo, Node node) {
        this.connectionInfo = connectionInfo;
        this.node = node;
    }

    public String getServiceName() {
        return node.getNameEn();
    }

    public String getUsername() {
        return connectionInfo.getUsername();
    }

    public String getPassword() {
        return connectionInfo.getPassword();
    }

    public String getConnectionHost() {
        return connectionInfo == null ? null : connectionInfo.getHost();
    }

    public int getConnectionPort() {
        return connectionInfo == null ? 0 : connectionInfo.getPort();
    }

    public int getPort() {
        return node == null ? 0 : node.getPort();
    }


    public String getHost() {
        return node == null ? null : node.getHost();
    }


    public int getParameter(String key, int defaultValue) {
        Map<String, String> ex = connectionInfo.getEx();
        String value = ex.get(key);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            log.warn(e.getMessage());
            return defaultValue;
        }
    }



    public String getParameter(String key) {
        return getParameter(key, StringConstants.EMPTY);
    }

    public String getParameter(String key, String defaultValue) {
        return connectionInfo.getParameter(key, defaultValue);
    }

    /**
     * Get the parameters to be selected(filtered)
     *
     * @param nameToSelect the {@link Predicate} to select the parameter name
     * @return non-null {@link Map}
     * @since 2.7.8
     */
    public Map<String, String> getParameters(Predicate<String> nameToSelect) {
        Map<String, String> selectedParameters = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : getParameters().entrySet()) {
            String name = entry.getKey();
            if (nameToSelect.test(name)) {
                selectedParameters.put(name, entry.getValue());
            }
        }
        return Collections.unmodifiableMap(selectedParameters);
    }

    public Map<String, String> getParameters() {
        return connectionInfo.getEx();
    }


    public String getAddress() {
        return connectionInfo.getAddress();
    }


    public String buildString(boolean useIp, String... parameters) {
        StringBuilder buf = new StringBuilder();
        String host;
        if (useIp) {
            host = connectionInfo.getIp();
        } else {
            host = getHost();
        }

        if (StringUtils.isNotEmpty(host)) {
            buf.append(host);
            if (getPort() > 0) {
                buf.append(':');
                buf.append(getPort());
            }
        }

        if (node != null) {
            buf.append(node.getNameEn());
            buf.append(':');
            buf.append(node.getPubMode());
        }

        for (String parameter : parameters) {
            buf.append(':');
            buf.append(parameter);
        }

        return buf.toString();
    }

    public ConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }

    public Node getNode() {
        return node;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("remote", connectionInfo.toString())
                .append("node", node.toString())
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass())  {
            return false;
        }
        Metadata metadata = (Metadata) o;
        return Objects.equals(connectionInfo, metadata.connectionInfo) && Objects.equals(node, metadata.node);
    }

    private volatile transient int hashCodeCache = -1;

    @Override
    public int hashCode() {
        if (hashCodeCache == -1) {
            hashCodeCache = Objects.hash(connectionInfo, node);
        }
        return hashCodeCache;
    }


}
