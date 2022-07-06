package com.hqy.rpc.api.protocol;

import cn.hutool.core.map.MapUtil;
import com.alibaba.nacos.common.utils.ArrayUtils;
import com.hqy.base.common.base.lang.exception.NoAvailableProviderException;
import com.hqy.base.common.base.lang.exception.RpcException;
import com.hqy.rpc.api.Invocation;
import com.hqy.rpc.api.Invoker;
import com.hqy.rpc.api.RpcInvocation;
import com.hqy.rpc.common.Metadata;
import com.hqy.util.AssertUtil;
import com.hqy.util.IpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * This Invoker works on Consumer side.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/6 15:19
 */
public abstract class AbstractInvoker<T> implements Invoker<T> {

    private static final Logger log = LoggerFactory.getLogger(AbstractInvoker.class);

    /**
     * Service interface type
     */
    private final Class<T> type;

    /**
     *  provider metadata
     */
    private final Metadata metadata;

    /**
     * {@link Invoker} default attachment
     */
    private final Map<String, Object> attachment;


    /**
     * {@link Metadata} available
     */
    private volatile boolean available = true;

    /**
     * {@link Metadata} destroy
     */
    private boolean destroyed = false;

    public AbstractInvoker(Class<T> type, Metadata metadata) {
        this(type, metadata, (Map<String, Object>) null);
    }

    public AbstractInvoker(Class<T> type, Metadata metadata, String[] keys) {
        this(type, metadata, convertAttachment(metadata, keys));
    }

    public AbstractInvoker(Class<T> type, Metadata metadata, Map<String, Object> attachment) {
        AssertUtil.notNull(type, "Service type should not be null.");
        AssertUtil.notNull(metadata, "Service metadata should not be null.");
        this.type = type;
        this.metadata = metadata;
        this.attachment =  attachment == null ? null : Collections.unmodifiableMap(attachment);
    }


    private static Map<String, Object> convertAttachment(Metadata metadata, String[] keys) {
        if (ArrayUtils.isEmpty(keys)) {
            return null;
        }
        Map<String, Object> attachment = new HashMap<>(keys.length);
        for (String key : keys) {
            String value = metadata.getParameter(key);
            if (value != null && value.length() > 0) {
                attachment.put(key, value);
            }
        }
        return attachment;
    }

    @Override
    public Object invoke(Invocation invocation) throws RpcException {
        if (isDestroyed()) {
           log.warn("Invoker for service " + this + " on consumer " + IpUtil.getHostAddress() + " is destroyed, this invoker should not be used any longer");
        }

        RpcInvocation rpcInvocation = (RpcInvocation) invocation;

        // prepare rpc invocation
        prepareInvocation(rpcInvocation);

        // do invoke rpc invocation and return
        return doInvokeAndReturn(rpcInvocation);
    }


    private void prepareInvocation(RpcInvocation rpcInvocation) {
        rpcInvocation.setInvoker(this);

        addInvocationAttachments(rpcInvocation);
    }

    private void addInvocationAttachments(RpcInvocation rpcInvocation) {
        // invoker attachment
        if (MapUtil.isNotEmpty(attachment)) {
            rpcInvocation.addObjectAttachmentsIfAbsent(attachment);
        }
    }

    private Object doInvokeAndReturn(RpcInvocation rpcInvocation) {
        Object result = null;
        try {
            result = doInvoke(rpcInvocation);
        } catch (InvocationTargetException e) {

        } catch (NoAvailableProviderException e) {

        } catch (ExecutionException e) {

        } catch (Throwable t) {

        }

        return result;
    }

    @Override
    public Class<T> getInterface() {
        return type;
    }

    @Override
    public Metadata getMetadata() {
        return metadata;
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    @Override
    public void destroy() {
        this.destroyed = true;
        setAvailable(false);
    }

    @Override
    public String toString() {
        return getInterface() + " -> " + (getMetadata() == null ? "" : getMetadata().getAddress());
    }


    public boolean isDestroyed() {
        return destroyed;
    }


    public void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * Specific implementation of the {@link #invoke(Invocation)} method
     */
    protected abstract Object doInvoke(Invocation invocation) throws Throwable;
}
