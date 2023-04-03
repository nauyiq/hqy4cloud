package com.hqy.cloud.rpc.cluster.loadbalance;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.rpc.Invoker;
import com.hqy.cloud.rpc.model.RPCModel;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * According to the weighted load balance
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/30 10:07
 */
public class RoundRobinLoadBalance extends AbstractLoadBalance {

    public static final String NAME = "roundRobin";

    /**
     * 每个 Invoker 都有一个 current 值，初始值为自身权重。在每个 Invoker 中current = current + weight。遍历完 Invoker 后，current 最大的那个 Invoker 就是本次选中的 Invoker。
     * 选中 Invoker 后，将本次 current 值计算current = current - totalWeight。
     * @param invokers   invokers
     * @param rpcModel   refer rpcContext
     * @param <T>
     * @return
     */
    @Override
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, RPCModel rpcModel) {
        String key = invokers.get(0).getInterface().getSimpleName();

        int totalWeight = 0;
        long maxCurrent = Long.MIN_VALUE;
        long now = System.currentTimeMillis();
        Invoker<T> selectedInvoker = null;
        WeightedRoundRobin selectedRoundRobin = null;
        for (Invoker<T> invoker : invokers) {
            // invoker weight roundRobin.
            int weight = getWeight(invoker);
            WeightedRoundRobin weightedRoundRobin = interfacesWeight.computeIfAbsent(key, k -> {
                WeightedRoundRobin roundRobin = new WeightedRoundRobin();
                roundRobin.setWeight(weight);
                return roundRobin;
            });

            if (weight != weightedRoundRobin.getWeight()) {
                //weight changed
                weightedRoundRobin.setWeight(weight);
            }

            long cur = weightedRoundRobin.increaseCurrent();
            weightedRoundRobin.setLastUpdate(now);
            if (cur > maxCurrent) {
                maxCurrent = cur;
                selectedInvoker = invoker;
                selectedRoundRobin = weightedRoundRobin;
            }
            totalWeight += weight;

            if (invokers.size() != interfacesWeight.size()) {
                interfacesWeight.entrySet().removeIf(item -> now - item.getValue().getLastUpdate() > RECYCLE_PERIOD);
            }

            if (selectedInvoker != null) {
                selectedRoundRobin.sel(totalWeight);
                return selectedInvoker;
            }

        }
        // should not happen here
        return  invokers.get(0);
    }

    private static final int RECYCLE_PERIOD = 60000;
    private final Map<String, WeightedRoundRobin> interfacesWeight = MapUtil.newConcurrentHashMap();

    protected static class WeightedRoundRobin {
        private int weight;
        private final AtomicLong current = new AtomicLong(0);
        private long lastUpdate;

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
            current.set(0);
        }

        public long increaseCurrent() {
            return current.addAndGet(weight);
        }

        public void sel(int total) {
            current.addAndGet(-1 * total);
        }

        public long getLastUpdate() {
            return lastUpdate;
        }

        public void setLastUpdate(long lastUpdate) {
            this.lastUpdate = lastUpdate;
        }
    }


}
