package com.hqy.cloud.rpc.cluster.router.master;

import com.hqy.cloud.registry.common.model.ApplicationModel;
import com.hqy.cloud.rpc.Invocation;
import com.hqy.cloud.rpc.Invoker;
import com.hqy.cloud.rpc.cluster.ClusterJoinConstants;
import com.hqy.cloud.rpc.cluster.router.AbstractRouter;
import com.hqy.cloud.rpc.cluster.router.RouterResult;
import com.hqy.cloud.rpc.model.RpcModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * select master node.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/5
 */
public class MasterNodeRouter<T> extends AbstractRouter<T> {
    private static final Logger log = LoggerFactory.getLogger(MasterNodeRouter.class);
    private static final String MASTER_ROUTER_PRIORITY_KEY = "masterRouter-priority";
    private static final int DEFAULT_PRIORITY = 5;

    public MasterNodeRouter(RpcModel rpcModel) {
        this.rpcModel = rpcModel;
        this.priority = rpcModel.getParameter(MASTER_ROUTER_PRIORITY_KEY, DEFAULT_PRIORITY);
    }

    @Override
    public RouterResult<Invoker<T>> route(List<Invoker<T>> invokers, Invocation invocation) {
        boolean queryMasterNode = (boolean) invocation.getObjectAttachments().getOrDefault(ClusterJoinConstants.MASTER, false);
        if (!queryMasterNode) {
            return new RouterResult<>(invokers, true);
        }
        try {
            Invoker<T> chooseInvoker = null;
            for (Invoker<T> invoker : invokers) {
                ApplicationModel model = invoker.getModel().getModel();
                if (model.getMetadataInfo().isMaster()) {
                    chooseInvoker = invoker;
                    break;
                }
            }

            if (chooseInvoker != null && chooseInvoker.isAvailable()) {
                return new RouterResult<>(Collections.singletonList(chooseInvoker), false);
            }
        } catch (Throwable t) {
            log.error("Failed to execute master router rule: " + getRpcModel() + ", invokers: " + invokers + ", cause: " + t.getMessage(), t);
        }
        return new RouterResult<>(Collections.emptyList(), false);
    }

    @Override
    public void notify(List<Invoker<T>> invokers) {
        super.notify(invokers);
    }
}
