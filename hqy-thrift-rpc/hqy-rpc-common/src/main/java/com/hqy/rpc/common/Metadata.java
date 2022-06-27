package com.hqy.rpc.common;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/23 12:17
 */
public class Metadata implements Serializable {

    private static final long serialVersionUID = -2179067844822981338L;

    private final Remote remote;

    private final Node node;

    public Metadata(Remote remote, Node node) {
        this.remote = remote;
        this.node = node;
    }

    public String getHost() {
        return remote == null ? null : remote.getHost();
    }

    public int getPort() {
        return remote == null ? 0 : remote.getPort();
    }


    public int getParameter(String key, int defaultValue) {
        Map<String, Object> ex = remote.getEx();
        Object o = ex.get(key);
        if (o instanceof Integer) {
            return (Integer) o;
        } else {
            return defaultValue;
        }
    }

    public String getAddress() {
        return remote.getAddress();
    }


    public String buildString(boolean useIp, String... parameters) {
        StringBuilder buf = new StringBuilder();
        String host;
        if (useIp) {
            host = remote.getIp();
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
            buf.append(node.getPubValue());
        }

        for (String parameter : parameters) {
            buf.append(':');
            buf.append(parameter);
        }

        return buf.toString();
    }

    public Remote getRemote() {
        return remote;
    }

    public Node getNode() {
        return node;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("remote", remote.toString())
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
        return Objects.equals(remote, metadata.remote) && Objects.equals(node, metadata.node);
    }

    private volatile transient int hashCodeCache = -1;

    @Override
    public int hashCode() {
        if (hashCodeCache == -1) {
            hashCodeCache = Objects.hash(remote, node);
        }
        return hashCodeCache;
    }


}
