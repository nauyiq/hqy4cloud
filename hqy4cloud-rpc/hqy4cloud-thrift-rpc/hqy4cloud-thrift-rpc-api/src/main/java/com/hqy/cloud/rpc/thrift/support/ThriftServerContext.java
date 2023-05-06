package com.hqy.cloud.rpc.thrift.support;

import com.hqy.cloud.rpc.model.RPCModel;

/**
 * ThriftServerContext.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/21 15:57
 */
public class ThriftServerContext extends Context {

    private final RPCModel rpcModel;
    private final String communicationParty;
    private boolean bind = false;
    public ThriftServerContext(String communicationParty, RPCModel rpcModel) {
        this.communicationParty = communicationParty;
        this.rpcModel = rpcModel;
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

    public RPCModel getRpcModel() {
        return rpcModel;
    }


}
