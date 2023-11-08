package com.hqy.cloud.rpc.model;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.common.base.lang.ActuatorNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * RPC中类似生命周期的模型.
 * 储存了RPC应用的基础元数据
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/1 9:48
 */
public abstract class ScopeModel {
    private static final Logger log = LoggerFactory.getLogger(ScopeModel.class);

    private final RPCModel selfModel;
    private String internalId;
    private final String modelName;
    private final String desc;

    private Map<String, Object> attributes;
    private Set<ScopeModelDeployerListener<ScopeModel>> deployerListeners;
    private final AtomicBoolean destroyed = new AtomicBoolean(false);

    public ScopeModel(RPCModel rpcModel) {
        this.selfModel = rpcModel;
        this.modelName = rpcModel.getName();
        this.desc = rpcModel.getDesc();
    }

    protected void initialize() {
        this.deployerListeners = new LinkedHashSet<>();
        this.attributes = MapUtil.newConcurrentHashMap();
    }


    public void destroy() {
        if (destroyed.compareAndSet(false, true)) {
            try {
                onDestroy();
            } catch (Throwable cause) {
                log.error("Error happened when destroying ScopeModel.", cause);
            }
        }
    }

    /**
     * do destroy.
     */
    public abstract void onDestroy();


    public boolean isDestroy() {
        return destroyed.get();
    }


    protected void notifyDestroy() {
        for (ScopeModelDeployerListener<ScopeModel> deployerListener : deployerListeners) {
            deployerListener.onDestroy(this);
        }
    }

    public final void addDeployerListener(ScopeModelDeployerListener<ScopeModel> listener) {
        this.deployerListeners.add(listener);
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key, Class<T> type) {
        return (T) attributes.get(key);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public String getInternalId() {
        return internalId;
    }

    public void setInternalId(String internalId) {
        this.internalId = internalId;
    }

    public String getDesc() {
        return desc;
    }

    public String getModelName() {
        return modelName;
    }

    public RPCModel getSelfModel() {
        return selfModel;
    }

    @Override
    public String toString() {
        return this.getDesc();
    }
}
