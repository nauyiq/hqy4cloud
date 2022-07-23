package com.hqy.rpc.cluster.router.hashfactor;

import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.rpc.api.Invoker;
import com.hqy.rpc.cluster.router.AbstractRouter;
import com.hqy.rpc.cluster.router.RouterResult;
import com.hqy.rpc.common.support.RPCModel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/1 11:01
 */
public class HashFactorRouter<T> extends AbstractRouter<T> {

    private static final Logger log = LoggerFactory.getLogger(HashFactorRouter.class);
    private static transient final String HASH_FACTOR_PRIORITY_KEY = "hashFactor-priority";
    private static transient final int DEFAULT_PRIORITY = 1;
//    private final boolean force;

    public HashFactorRouter(RPCModel rpcModel) {
        this.rpcModel = rpcModel;
        this.priority = rpcModel.getParameter(HASH_FACTOR_PRIORITY_KEY, DEFAULT_PRIORITY);
//        this.force = Boolean.parseBoolean(rpcContext.getParameter(FORCE_KEY, "false"));
    }

    @Override
    public RouterResult<Invoker<T>> route(List<Invoker<T>> invokers, RPCModel rpcModel) {
        String hashFactor = rpcModel.getHashFactor();
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
            log.error("Failed to execute hashFactor router rule: " + getContext() + ", invokers: " + invokers + ", cause: " + t.getMessage(), t);
        }
        return new RouterResult<>(new ArrayList<>(), false);
    }

    private boolean checkHashFactorAvailable(String[] hashFactorSplit) {
        return hashFactorSplit.length == 2 && hashFactorSplit[1] != null && StringUtils.isNumeric(hashFactorSplit[1]);
    }
}
