package com.hqy.cloud.rpc.protocol;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.common.base.lang.exception.RpcException;
import com.hqy.cloud.rpc.Invocation;
import com.hqy.cloud.rpc.Invoker;
import com.hqy.cloud.rpc.core.RPCContext;
import com.hqy.cloud.rpc.model.RPCModel;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.IpUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
     *  rpc model
     */
    private final RPCModel rpcModel;

    /**
     * consumer rpc model.
     */
    private final RPCModel consumerModel;

    /**
     * {@link Invoker} default attachment
     */
    private final Map<String, Object> attachment;

    /**
     * {@link RPCModel} available
     */
    private volatile boolean available = true;

    /**
     * {@link RPCModel} destroy
     */
    private boolean destroyed = false;

    public AbstractInvoker(Class<T> type, RPCModel model, RPCModel consumerModel) {
        this(type, model, consumerModel, (Map<String, Object>) null);
    }

    public AbstractInvoker(Class<T> type, RPCModel model, RPCModel consumerModel, String[] keys) {
        this(type, model, consumerModel, convertAttachment(model, keys));
    }

    public AbstractInvoker(Class<T> type, RPCModel context, RPCModel consumerModel, Map<String, Object> attachment) {
        AssertUtil.notNull(type, "Service type should not be null.");
        AssertUtil.notNull(context, "Service context should not be null.");
        this.type = type;
        this.rpcModel = context;
        this.consumerModel = consumerModel;
        this.attachment = attachment == null ? null : Collections.unmodifiableMap(attachment);
    }


    private static Map<String, Object> convertAttachment(RPCModel rpcModel, String[] keys) {
        if (ArrayUtils.isEmpty(keys)) {
            return null;
        }
        Map<String, Object> attachment = new HashMap<>(keys.length);
        for (String key : keys) {
            String value = rpcModel.getParameter(key);
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
        // prepare rpc invocation
        prepareInvocation(invocation);
        // do invoke rpc invocation and return
        return doInvokeAndReturn(invocation);
    }


    private void prepareInvocation(Invocation rpcInvocation) {
        try {
            doPrepareInvoke(rpcInvocation);
            //create this rpc request context.
            createRpcContext(rpcInvocation);
        } catch (Throwable t) {
            log.warn("Prepare invoke happen error, cause {}", t.getMessage(), t);
        }
        addInvocationAttachments(rpcInvocation);
    }

    private void createRpcContext(Invocation rpcInvocation) {
        RPCContext rpcContext = RPCContext.builder()
                .rpcModel(getConsumerModel())
                .serviceClass(getInterface())
                .caller(getConsumerModel().getName())
                .provider(getModel().getName())
                .consumerAddress(getConsumerModel().getServerAddress())
                .providerAddress(getModel().getServerAddress())
                .method(rpcInvocation.getMethodName())
                .arguments(rpcInvocation.getArguments())
                .parameterTypes(rpcInvocation.getParameterTypes())
                .request(this).build();
        RPCContext.setRpcContext(rpcContext);
    }




    private void addInvocationAttachments(Invocation rpcInvocation) {
        // invoker attachment
        if (MapUtil.isNotEmpty(attachment)) {
            rpcInvocation.addObjectAttachmentsIfAbsent(attachment);
        }
    }

    private Object doInvokeAndReturn(Invocation invocation) throws RpcException {
        try {
            return doInvoke(invocation);
        } catch (RpcException cause) {
            log.warn(cause.getMessage());
            throw cause;
        } finally {
            releaseRpcContext();
        }
    }

    private void releaseRpcContext() {
        RPCContext.removeRpcContext();
    }

    @Override
    public Class<T> getInterface() {
        return type;
    }

    @Override
    public RPCModel getModel() {
        return rpcModel;
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
        return getInterface() + " -> " + (getModel() == null ? "" : getModel().getServerAddress());
    }


    public boolean isDestroyed() {
        return destroyed;
    }


    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public RPCModel getConsumerModel() {
        return consumerModel;
    }

    /**
     * Specific implementation of the {@link #invoke(Invocation)} method
     * @param invocation    {@link Invocation}
     * @return               rpc result.
     * @throws RpcException  exception.
     */
    protected abstract Object doInvoke(Invocation invocation) throws RpcException;


    /**
     * prepare invoke.
     * @param  invocation {@link Invocation}
     * @throws RpcException exception.
     */
    protected abstract void doPrepareInvoke(Invocation invocation) throws RpcException;
}
