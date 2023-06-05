package com.hqy.cloud.rpc.model;

import com.hqy.cloud.rpc.CloseableService;
import com.hqy.cloud.rpc.threadpool.ExecutorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/6/1 16:21
 */
public abstract class ModuleModel extends ScopeModel {
    protected final static Logger log = LoggerFactory.getLogger(ModuleModel.class);
    private final CloseableService service;

    public ModuleModel(RPCModel rpcModel, CloseableService service) {
        super(rpcModel);
        this.service = service;
    }

    protected void initialize(ExecutorRepository repository) {
        super.initialize();
        doInit(repository);
    }

    /**
     * do init.
     * @param repository executor repository
     */
    protected abstract void doInit(ExecutorRepository repository);


    @Override
    public void onDestroy() {
        if (!service.isAvailable()) {
            log.info("The model already destroy.");
            return;
        }
        service.destroy();
    }






}
