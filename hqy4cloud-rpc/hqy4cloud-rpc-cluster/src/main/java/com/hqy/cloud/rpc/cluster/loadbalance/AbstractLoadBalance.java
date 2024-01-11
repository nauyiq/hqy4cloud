package com.hqy.cloud.rpc.cluster.loadbalance;

import com.hqy.cloud.rpc.Invoker;
import com.hqy.cloud.rpc.model.RpcModel;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

import static com.hqy.cloud.rpc.CommonConstants.DEFAULT_WARMUP;
import static com.hqy.cloud.rpc.CommonConstants.WARMUP;


/**
 * AbstractLoadBalance.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/29 17:38
 */
public abstract class AbstractLoadBalance implements LoadBalance {

    /**
     * child class impl -> select one invoker in list.
     * @param invokers      invokers
     * @param rpcModel    refer rpcContext
     * @return              selected invoker.
     */
    protected abstract <T> Invoker<T> doSelect(List<Invoker<T>> invokers, RpcModel rpcModel);


    @Override
    public <T> Invoker<T> select(List<Invoker<T>> invokers, RpcModel rpcModel) {
        if (CollectionUtils.isEmpty(invokers)) {
            return null;
        }
        return invokers.size() == 1 ? invokers.get(0) : doSelect(invokers, rpcModel);
    }

    /**
     * Get the weight of the invoker's invocation which takes warmup time into account
     * if the uptime is within the warmup time, the weight will be reduce proportionally
     * @param invoker    the invoker
     * @return weight
     */
    protected int getWeight(Invoker<?> invoker) {
        RpcModel rpcModel = invoker.getModel();
        int weight = rpcModel.getWeight();
        if (weight > 0) {
            long timestamp = rpcModel.serverStartTimestamp();
            if (timestamp > 0L) {
                long uptime = System.currentTimeMillis() - timestamp;
                if (uptime < 0) {
                    return 1;
                }
                int warmup = rpcModel.getParameter(WARMUP, DEFAULT_WARMUP);
                if (uptime > 0 && uptime < warmup) {
                    weight = calculateWarmupWeight((int)uptime, warmup, weight);
                }
            }
        }
        return Math.max(weight, 0);
    }

    /**
     * Calculate the weight according to the uptime proportion of warmup time
     * the new weight will be within 1(inclusive) to weight(inclusive)
     *
     * @param uptime the uptime in milliseconds
     * @param warmup the warmup time in milliseconds
     * @param weight the weight of an invoker
     * @return weight which takes warmup into account
     */
    static int calculateWarmupWeight(int uptime, int warmup, int weight) {
        int ww = (int) ( uptime / ((float) warmup / weight));
        return ww < 1 ? 1 : (Math.min(ww, weight));
    }

}
