package com.hqy.rpc.monitor.thrift.api;

import com.hqy.rpc.monitor.CollectionData;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/18 16:17
 */
public class Statistics implements Serializable {
    private static final long serialVersionUID = 8047755787379288681L;

    private String caller;

    private String provider;

    public Statistics(CollectionData data) {
        this.caller = data.getCaller();
        this.provider = data.getProvider();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Statistics that = (Statistics) o;
        return Objects.equals(caller, that.caller) && Objects.equals(provider, that.provider);
    }

    @Override
    public int hashCode() {
        return Objects.hash(caller, provider);
    }


    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }


}
