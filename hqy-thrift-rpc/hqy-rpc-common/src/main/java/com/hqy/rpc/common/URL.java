package com.hqy.rpc.common;

import com.google.common.base.Objects;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/23 12:17
 */
public class URL implements Serializable {

    private static final long serialVersionUID = -2179067844822981338L;

    private final URLAddress urlAddress;

    private final Node node;

    protected URL(URLAddress urlAddress) {
        this(urlAddress, null);
    }

    protected URL(URLAddress urlAddress, Node node) {
        this.urlAddress = urlAddress;
        this.node = node;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("urlAddress", urlAddress)
                .append("node", node.toString())
                .toString();
    }

    public String getHost() {
        return urlAddress == null ? null : urlAddress.getHost();
    }

    public int getPort() {
        return urlAddress == null ? 0 : urlAddress.getPort();
    }

    public int getParameter(String key, int defaultValue) {
        Map<String, Object> metadata = node.getMetadata();
        Object o = metadata.get(key);
        if (o instanceof Integer) {
            return (Integer) o;
        } else {
            return defaultValue;
        }

    }


    public String buildString(boolean useIp, String... parameters) {
        StringBuilder buf = new StringBuilder();
        String host;
        if (useIp) {
            host = urlAddress.getIp();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        URL url = (URL) o;
        return Objects.equal(urlAddress, url.urlAddress) && Objects.equal(node, url.node);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(urlAddress, node);
    }
}
