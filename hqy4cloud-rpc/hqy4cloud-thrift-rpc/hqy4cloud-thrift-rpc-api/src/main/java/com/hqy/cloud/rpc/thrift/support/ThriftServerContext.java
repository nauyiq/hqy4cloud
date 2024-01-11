package com.hqy.cloud.rpc.thrift.support;

import com.hqy.cloud.rpc.model.RpcModel;

/**
 * ThriftServerContext.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/21 15:57
 */
public class ThriftServerContext extends Context {

    private final RpcModel rpcModel;
    private final String communicationParty;
    private boolean bind = false;
    private final String methodName;
    private final String serviceTypeName;

    public ThriftServerContext(String methodName, String serviceTypeName, String communicationParty, RpcModel rpcModel) {
        this.methodName = methodName;
        this.serviceTypeName = serviceTypeName;
        this.communicationParty = communicationParty;
        this.rpcModel = rpcModel;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getServiceTypeName() {
        return serviceTypeName;
    }

    public String getCommunicationParty() {
        return communicationParty;
    }

    public boolean isBind() {
        return bind;
    }

    public void setBind(boolean bind) {
        this.bind = bind;
    }

    public RpcModel getRpcModel() {
        return rpcModel;
    }


}
