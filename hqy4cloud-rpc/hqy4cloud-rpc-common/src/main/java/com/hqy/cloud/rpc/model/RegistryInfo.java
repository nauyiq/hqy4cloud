package com.hqy.cloud.rpc.model;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.base.Parameters;
import com.hqy.cloud.rpc.CommonConstants;
import com.hqy.cloud.util.IpUtil;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * server connection 2 registry information
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/27 16:44
 */
public class RegistryInfo extends Parameters implements Serializable {
    @Serial
    private static final long serialVersionUID = -508803540161945493L;

    /**
     * register application name
     */
    protected String name;

    /**
     * registry host address.
     */
    protected String host;

    /**
     * registry connection transport port.
     */
    protected int port;

    /**
     * raw address.
     */
    protected transient String rawAddr;


    public RegistryInfo(String name, String rawAddr) {
        this(name,null, 0, rawAddr);
    }

    public RegistryInfo(String name, String host, int port) {
        this(name, host, port, null);
    }

    public RegistryInfo(String name, String host, int port, String rawAddr) {
        this(name, host, port, rawAddr, MapUtil.newConcurrentHashMap());
    }

    public RegistryInfo(String name, String host, int port, String rawAddr, Map<String, String> connectionParams) {
        this.name = name;
        this.host = host;
        port = Math.max(port, 0);
        this.port = port;
        this.rawAddr = rawAddr;
        super.parameters = connectionParams;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() { return port; }

    public void setPort(int port) {
        this.port = port;
    }

    public RegistryInfo setAddress(String host, int port) {
        return new RegistryInfo(this.getName(), host, port, rawAddr);
    }

    public String getAddress() {
        if (rawAddr == null) {
            rawAddr = getAddress(getHost(), getPort());
        }
        return rawAddr;
    }

    protected String getAddress(String host, int port) {
        return port <= 0 ? host : host + StringConstants.Symbol.COLON + port;
    }

    public String getIp() {
        return IpUtil.getIpByHost(getHost());
    }

    public Map<String, String> getConnectionParams() {
        return super.parameters;
    }

    public String getUsername() {
        return getParameter(CommonConstants.USERNAME);
    }

    public String getPassword() {
        return getParameter(CommonConstants.PASSWORD);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("host", host)
                .append("port", port)
                .append("rawAddress", rawAddr)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegistryInfo registryInfo = (RegistryInfo) o;
        return port == registryInfo.port  && Objects.equals(host, registryInfo.host) && Objects.equals(rawAddr, registryInfo.rawAddr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, rawAddr);
    }


}
