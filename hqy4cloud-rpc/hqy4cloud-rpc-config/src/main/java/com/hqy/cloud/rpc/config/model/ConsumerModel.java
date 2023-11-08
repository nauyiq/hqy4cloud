package com.hqy.cloud.rpc.config.model;

import com.hqy.cloud.rpc.cluster.client.Client;
import com.hqy.cloud.rpc.model.ModuleModel;
import com.hqy.cloud.rpc.model.RPCModel;
import com.hqy.cloud.rpc.threadpool.ExecutorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ConsumerModel.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/1 16:02
 */
public class ConsumerModel extends ModuleModel {
    private static final Logger log = LoggerFactory.getLogger(ConsumerModel.class);
    private final Client client;

    public ConsumerModel(RPCModel rpcModel, Client client) {
        super(rpcModel, client);
        this.client = client;
    }

    @Override
    protected void doInit(ExecutorRepository repository) {
        this.client.initialize(repository);
    }

}
