package com.hqy.cloud.rpc.cluster.router.hashfactor;

import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.rpc.Invocation;
import com.hqy.cloud.rpc.Invoker;
import com.hqy.cloud.rpc.cluster.ClusterJoinConstants;
import com.hqy.cloud.rpc.cluster.router.AbstractRouter;
import com.hqy.cloud.rpc.cluster.router.RouterResult;
import com.hqy.cloud.rpc.model.RpcModel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * HashFactorRouter.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/1
 */
public class HashFactorRouter<T> extends AbstractRouter<T> {
    private static final Logger log = LoggerFactory.getLogger(HashFactorRouter.class);

    private static transient final String HASH_FACTOR_PRIORITY_KEY = "hashFactor-priority";
    private static transient final int DEFAULT_PRIORITY = 1;

    public HashFactorRouter(RpcModel rpcModel) {
        this.rpcModel = rpcModel;
        this.priority = rpcModel.getParameter(HASH_FACTOR_PRIORITY_KEY, DEFAULT_PRIORITY);
    }

    @Override
    public RouterResult<Invoker<T>> route(List<Invoker<T>> invokers, Invocation invocation) {
        String hashFactor = (String) invocation.getObjectAttachments().getOrDefault(ClusterJoinConstants.HASH_FACTOR, StringConstants.DEFAULT);
        boolean hashFactorRoute = StringUtils.isNotEmpty(hashFactor) && !hashFactor.equals(StringConstants.DEFAULT);
        if (log.isDebugEnabled()) {
            log.debug("HashFactorRouter -> consumer hashFactor: {}", hashFactor);
        }
        if (!hashFactorRoute) {
            //This rule invalid, continue next router.
            return new RouterResult<>(invokers, true);
        }

        try {
            String[] hashFactorSplit = hashFactor.split(StringConstants.Symbol.COLON);
            boolean checkHashFactorAvailable = checkHashFactorAvailable(hashFactorSplit);
            if (checkHashFactorAvailable) {
                //hashFactor disabled, break router.
                return new RouterResult<>(new ArrayList<>(), false);
            }

            Invoker<T> chooseInvoker = null;
            for (Invoker<T> invoker : invokers) {
                String invokerHashFactor = invoker.getModel().getHashFactor();
                if (!checkHashFactorAvailable(invokerHashFactor.split(StringConstants.Symbol.COLON))) {
                    continue;
                }
                if (invokerHashFactor.equals(hashFactor)) {
                    chooseInvoker = invoker;
                    break;
                }
            }
            if (chooseInvoker != null && chooseInvoker.isAvailable()) {
                return new RouterResult<>(Collections.singletonList(chooseInvoker), false);
            }
        } catch (Throwable t) {
            log.error("Failed to execute hashFactor router rule: " + getRpcModel() + ", invokers: " + invokers + ", cause: " + t.getMessage(), t);
        }
        return new RouterResult<>(new ArrayList<>(), false);
    }

    private boolean checkHashFactorAvailable(String[] hashFactorSplit) {
        return hashFactorSplit.length == 2 && hashFactorSplit[1] != null && StringUtils.isNumeric(hashFactorSplit[1]);
    }
}
