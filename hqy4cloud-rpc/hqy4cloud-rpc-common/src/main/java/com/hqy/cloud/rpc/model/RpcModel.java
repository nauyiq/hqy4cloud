package com.hqy.cloud.rpc.model;

import com.hqy.cloud.common.base.Parameters;
import com.hqy.cloud.registry.common.model.ApplicationModel;
import com.hqy.cloud.rpc.CommonConstants;
import com.hqy.cloud.util.AssertUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * RPCModel- Uniform Resource Locator
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/7
 */
public class RpcModel extends Parameters implements Serializable {
    @Serial
    private static final long serialVersionUID = -1724139538145932293L;
    private static final Logger log = LoggerFactory.getLogger(RpcModel.class);

    /**
     * application name
     */
    private final String name;

    /**
     * application model
     */
    private final ApplicationModel model;

    /**
     * rpc metadata.
     */
    private RpcMetadata metadata;

    /**
     * this context create timestamp
     */
    private final long createTimestamp;



    /**
     * invoke service name cache
     */
    private final Set<String> invokedServiceClassCache = new CopyOnWriteArraySet<>();


    public RpcModel(ApplicationModel model) {
        AssertUtil.notNull(model, "Application model should not be null.");
        this.name = model.getApplicationName();
        this.model = model;
        this.metadata = RpcMetadata.of(model);
        this.createTimestamp = System.currentTimeMillis();
    }

    private RpcModel(String application, RpcModel copyModel) {
        this.name = application;
        ApplicationModel copyApplicationModel = copyModel.getModel();
        this.model = ApplicationModel.of(application, copyApplicationModel.getNamespace(), copyApplicationModel.getGroup());
        this.parameters = copyModel.parameters;
        this.createTimestamp = System.currentTimeMillis();
    }

    public static RpcModel copyOf(String application, RpcModel copyModel) {
        return new RpcModel(application, copyModel);
    }


    public void setRpcServiceInfo(List<RpcServiceInfo> serviceInfos) {
        if (CollectionUtils.isNotEmpty(serviceInfos)) {
            this.metadata.setRpcServiceInfos(serviceInfos);
        }
    }

    public RpcServerAddress getServerAddress() {
        return metadata.getRpcServerAddress();
    }

    public ApplicationModel getModel() {
        return model;
    }

    public String getName() {
        return name;
    }



    public long getCreateTimestamp() {
        return createTimestamp;
    }

    public int getWeight() {
       return this.model.getMetadataInfo().getWeight();
    }

    public long serverStartTimestamp() {
        return getParameter(CommonConstants.START_SERVER_TIMESTAMP, getCreateTimestamp());
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

    public Set<String> getInvokedServiceClassCache() {
        return invokedServiceClassCache;
    }

    public void addServiceClass(Class<?> clazz) {
        this.invokedServiceClassCache.add(clazz.getSimpleName());
    }

    public void setServerAddress(RpcServerAddress serverAddress) {
        this.metadata.setRpcServerAddress(serverAddress);
    }

    @Override
    public String toString() {
        return "RpcModel{" +
                "model=" + model +
                ", metadata=" + metadata +
                ", createTimestamp=" + createTimestamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RpcModel rpcModel = (RpcModel) o;
        return Objects.equals(name, rpcModel.name) && Objects.equals(model, rpcModel.model) && Objects.equals(metadata, rpcModel.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, model, metadata);
    }

    public int getPubMode() {
        return this.model.getMetadataInfo().getPubMode().value;
    }

    public String getServerHost() {
        return this.model.getHost();
    }

    public String getHashFactor() {
        return this.metadata.getHashFactor();
    }

    public RpcMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(RpcMetadata metadata) {
        this.metadata = metadata;
    }
}
