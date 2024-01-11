package com.hqy.cloud.registry.common.model;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.registry.common.deploy.DeployMetaDataService;
import com.hqy.cloud.registry.common.deploy.ModelDeployerListener;
import com.hqy.cloud.util.AssertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 封装了服务部署过程中的生命周期，储存了服务部署的元数据
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/29
 */
public abstract class DeployModel implements ModelService, DeployMetaDataService {
    private static final Logger log = LoggerFactory.getLogger(DeployModel.class);

    private final ApplicationModel model;
    private String internalId;
    private final String appName;
    private final String desc;

    private Map<String, Object> attributes;
    private Set<ModelDeployerListener<DeployModel>> deployerListeners;
    private final AtomicBoolean destroyed = new AtomicBoolean(false);

    public DeployModel(ApplicationModel model) {
        AssertUtil.notNull(model, "Application model should not be null.");
        this.model = model;
        this.appName = model.getApplicationName();
        this.desc = model.getApplicationDesc();
    }

    public void initialize() {
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

    public void start() {

    }


    public boolean isDestroy() {
        return destroyed.get();
    }

    protected void notifyDestroy() {
        for (ModelDeployerListener<DeployModel> deployerListener : deployerListeners) {
            deployerListener.onDestroy(this);
        }
    }

    public final void addDeployerListener(ModelDeployerListener<DeployModel> deployerListener) {
        this.deployerListeners.add(deployerListener);
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

    @Override
    public ApplicationModel getModel() {
        return model;
    }

    public String getAppName() {
        return appName;
    }

    @Override
    public String toString() {
        return this.getDesc();
    }
}
