package com.hqy.rpc.cluster.loadbalance;

import com.hqy.rpc.api.Invocation;
import com.hqy.rpc.api.Invoker;
import com.hqy.rpc.common.Metadata;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This class select one provider from multiple providers randomly.
 * You can define weights for each provider:
 * If the weights are all the same then it will use random.nextInt(number of invokers).
 * If the weights are different then it will use random.nextInt(w1 + w2 + ... + wn)
 * Note that if the performance of the machine is better than others, you can set a larger weight.
 * If the performance is not so good, you can set a smaller weight.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/30 11:03
 */
public class RandomLoadBalance extends AbstractLoadBalance {

    public static final String NAME = "random";

    /**
     * Select one invoker between a list using a random criteria
     * @param invokers List of possible invokers
     * @param metadata metadata
     * @param invocation Invocation
     * @return The selected invoker
     */
    @Override
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, Metadata metadata, Invocation invocation) {// Number of invokers
        int length = invokers.size();
        if (!needWeightLoadBalance(invokers,invocation)){
            return invokers.get(ThreadLocalRandom.current().nextInt(length));
        }

        // Every invoker has the same weight?
        boolean sameWeight = true;
        // the maxWeight of every invokers, the minWeight = 0 or the maxWeight of the last invoker
        int[] weights = new int[length];
        // The sum of weights
        int totalWeight = 0;
        for (int i = 0; i < length; i++) {
            int weight = getWeight(invokers.get(i), invocation);
            // Sum
            totalWeight += weight;
            // save for later use
            weights[i] = totalWeight;
            if (sameWeight && totalWeight != weight * (i + 1)) {
                sameWeight = false;
            }
        }
        if (totalWeight > 0 && !sameWeight) {
            // If (not every invoker has the same weight & at least one invoker's weight>0), select randomly based on totalWeight.
            int offset = ThreadLocalRandom.current().nextInt(totalWeight);
            // Return a invoker based on the random value.
            for (int i = 0; i < length; i++) {
                if (offset < weights[i]) {
                    return invokers.get(i);
                }
            }
        }
        // If all invokers have the same weight value or totalWeight=0, return evenly.
        return invokers.get(ThreadLocalRandom.current().nextInt(length));
    }

    private <T> boolean needWeightLoadBalance(List<Invoker<T>> invokers, Invocation invocation) {
        Invoker<T> invoker = invokers.get(0);
        Metadata metadata = invoker.getMetadata();
        int weight = metadata.getWeight();
        return weight != 0;
    }
}
