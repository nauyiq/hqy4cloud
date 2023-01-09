package com.hqy.rpc.common.support;

import com.hqy.base.common.swticher.CommonSwitcher;
import com.hqy.rpc.common.RPCServerAddress;
import com.hqy.rpc.common.threadlocal.InternalThreadLocal;
import com.hqy.rpc.common.transaction.TransactionContext;
import lombok.Builder;

/**
 * RPCContext.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/18 17:46
 */
@Builder
public class RPCContext {

    private static final InternalThreadLocal<RPCContext> RPC_CONTEXT = new InternalThreadLocal<>();

    private RPCModel rpcModel;

    private String method;

    private Class<?> serviceClass;

    private Class<?>[] parameterTypes;

    private Object[] arguments;

    private String caller;

    private String provider;

    private RPCServerAddress consumerAddress;

    private RPCServerAddress providerAddress;

    private Object request;

    public RPCContext() {
    }

    public RPCContext(RPCModel rpcModel, String method, Class<?> serviceClass, Class<?>[] parameterTypes, Object[] arguments, String caller, String provider, RPCServerAddress consumerAddress, RPCServerAddress providerAddress, Object request) {
        this.rpcModel = rpcModel;
        this.method = method;
        this.serviceClass = serviceClass;
        this.parameterTypes = parameterTypes;
        this.arguments = arguments;
        this.caller = caller;
        this.provider = provider;
        this.consumerAddress = consumerAddress;
        this.providerAddress = providerAddress;
        this.request = request;
    }

    public boolean needCollect(String methodName) {
        return RemoteContextChecker.needCollect(methodName);
    }

    public static boolean isGlobalTransactionalMethod(String methodName) {
        return TransactionContext.isTransactional(methodName) && CommonSwitcher.ENABLE_PROPAGATE_GLOBAL_TRANSACTION.isOn();
    }

    @SuppressWarnings("unchecked")
    public <T> T getRequest(Class<T> clazz) {
        return (request != null && clazz.isAssignableFrom(request.getClass())) ? (T) request : null;
    }

    public static RPCContext getRpcContext() {
        return RPC_CONTEXT.get();
    }

    public static void setRpcContext(RPCContext rpcContext) {
        RPC_CONTEXT.set(rpcContext);
    }

    public static void removeRpcContext() {
        RPC_CONTEXT.remove();
    }


    public RPCModel getRpcModel() {
        return rpcModel;
    }

    public String getMethod() {
        return method;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public String getCaller() {
        return caller;
    }

    public String getProvider() {
        return provider;
    }

    public Class<?> getServiceClass() {
        return serviceClass;
    }

    public void setRpcModel(RPCModel rpcModel) {
        this.rpcModel = rpcModel;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setServiceClass(Class<?> serviceClass) {
        this.serviceClass = serviceClass;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void setConsumerAddress(RPCServerAddress consumerAddress) {
        this.consumerAddress = consumerAddress;
    }

    public void setProviderAddress(RPCServerAddress providerAddress) {
        this.providerAddress = providerAddress;
    }

    public void setRequest(Object request) {
        this.request = request;
    }

    public RPCServerAddress getConsumerAddress() {
        return consumerAddress;
    }

    public RPCServerAddress getProviderAddress() {
        return providerAddress;
    }




}
