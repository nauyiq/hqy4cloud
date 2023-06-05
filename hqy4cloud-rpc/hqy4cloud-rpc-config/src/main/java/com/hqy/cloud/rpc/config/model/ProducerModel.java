package com.hqy.cloud.rpc.config.model;

import com.hqy.cloud.rpc.model.ModuleModel;
import com.hqy.cloud.rpc.model.RPCModel;
import com.hqy.cloud.rpc.server.RPCServer;
import com.hqy.cloud.rpc.threadpool.ExecutorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ProducerModel.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/1 14:31
 */
public class ProducerModel extends ModuleModel {
    private final static Logger log = LoggerFactory.getLogger(ProducerModel.class);
    private final RPCServer rpcServer;

    public ProducerModel(RPCModel rpcModel, RPCServer server) {
        super(rpcModel, server);
        this.rpcServer = server;
    }

    @Override
    protected void doInit(ExecutorRepository repository) {
        this.rpcServer.initialize(repository);
    }

}
