package com.hqy.rpc.cluster.router.gray;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.rpc.api.Invoker;
import com.hqy.rpc.cluster.router.AbstractRouter;
import com.hqy.rpc.cluster.router.RouterResult;
import com.hqy.rpc.common.support.RPCModel;
import com.hqy.util.IpUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/30 17:41
 */
public class GrayModeRouter<T> extends AbstractRouter<T> {

    private static final Logger log = LoggerFactory.getLogger(GrayModeRouter.class);

    private static transient final String GRAY_PRIORITY_KEY = "gray-mode-priority";
    private static transient final int DEFAULT_PRIORITY = 2;
    private final boolean force;

    /**
     * Refresh when the cache is empty for an hour.
     */
    private final Cache<Integer, List<Invoker<T>>> MODE_INSTANCES = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build();
    private final Cache<String, List<Invoker<T>>> HOST_INSTANCES = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build();


    public GrayModeRouter(RPCModel rpcModel) {
        this.rpcModel = rpcModel;
        this.priority = rpcModel.getParameter(GRAY_PRIORITY_KEY, DEFAULT_PRIORITY);
        this.force = Boolean.parseBoolean(rpcModel.getParameter(FORCE_KEY, "false"));
    }

    @Override
    public RouterResult<Invoker<T>> route(List<Invoker<T>> invokers, RPCModel rpcModel) {
        if (CollectionUtils.isEmpty(invokers) || rpcModel == null) {
            return new RouterResult<>(invokers, false);
        }
        boolean colorModeRpcRemote = CommonSwitcher.ENABLE_GRAY_MECHANISM.isOn();
        boolean sameIpHighPriority = CommonSwitcher.ENABLE_RPC_SAME_IP_HIGH_PRIORITY.isOn();
        if (log.isDebugEnabled()) {
            log.debug("GrayModeRouter -> Switcher 200 {} | Switcher 207 {}, ", colorModeRpcRemote, sameIpHighPriority);
        }
        if (!colorModeRpcRemote && !sameIpHighPriority) {
            //This rule invalid, continue next router.
            return new RouterResult<>(invokers, true);
        }

        try {
            List<Invoker<T>> chooseInvokers = findSuitableInvokers(colorModeRpcRemote, sameIpHighPriority, rpcModel, invokers);
            if (CollectionUtils.isNotEmpty(chooseInvokers)) {
                return new RouterResult<>(chooseInvokers, true);
            } else if (force) {
                log.warn("The route result is empty and force execute. consumer: " + IpUtil.getHostAddress() + ", service: " + rpcModel.getName());
                new RouterResult<>(chooseInvokers, true);
            }
        } catch (Throwable t) {
            log.error("Failed to execute GrayMode router rule: " + getContext() + ", invokers: " + invokers + ", cause: " + t.getMessage(), t);
        }

        return new RouterResult<>(invokers, true);
    }

    private  List<Invoker<T>> findSuitableInvokers(boolean colorModeRpcRemote, boolean sameIpHighPriority, RPCModel rpcModel, List<Invoker<T>> invokers) {
        List<Invoker<T>> chooseInvokers = new ArrayList<>(invokers);
        if (colorModeRpcRemote) {
            chooseInvokers = findGaryModeInvokers(rpcModel, invokers);
            if (CollectionUtils.isEmpty(chooseInvokers)) {
                return chooseInvokers;
            }
        }
        if (sameIpHighPriority) {
            List<Invoker<T>> sameIpInvokers = findSameIpInvokers(rpcModel, chooseInvokers);
            if (CollectionUtils.isNotEmpty(sameIpInvokers)) {
                chooseInvokers = sameIpInvokers;
            }
        }
        return chooseInvokers;
    }

    private List<Invoker<T>> findGaryModeInvokers(RPCModel consumerContext, List<Invoker<T>> invokers) {
        try {
            int consumerPubModeValue = consumerContext.getPubMode();
            // only call services of the same color as current server.
            List<Invoker<T>> colorInvokers = MODE_INSTANCES.getIfPresent(consumerPubModeValue);
            if (CollectionUtils.isEmpty(colorInvokers)) {
                colorInvokers = invokers.stream().filter(invoker -> invoker.getModel().getPubMode() == consumerPubModeValue).collect(Collectors.toList());
                MODE_INSTANCES.put(consumerPubModeValue, colorInvokers);
            }
            return colorInvokers;
        } catch (Throwable t) {
            log.warn("Failed to execute gray mode router rule, invokers: {}", invokers);
            return invokers;
        }

    }

    private List<Invoker<T>> findSameIpInvokers(RPCModel rpcModel, List<Invoker<T>> invokers) {
        try {
            String host = rpcModel.getServerHost();
            List<Invoker<T>> hostInvokers = HOST_INSTANCES.getIfPresent(host);
            if (CollectionUtils.isEmpty(hostInvokers)) {
                hostInvokers = invokers.stream().filter(invoker -> invoker.getModel().getServerHost().equals(host)).collect(Collectors.toList());
                HOST_INSTANCES.put(host, hostInvokers);
            }
            return hostInvokers;
        } catch (Throwable t) {
            log.warn("Failed to execute same ip router rule, invokers: {}", invokers);
            return invokers;
        }
    }

    @Override
    public void notify(List<Invoker<T>> invokers) {
        super.notify(invokers);
        MODE_INSTANCES.invalidateAll();
        HOST_INSTANCES.invalidateAll();

        if (CollectionUtils.isNotEmpty(invokers)) {
            Map<Integer, List<Invoker<T>>> invokerModeMap = invokers.stream().collect(Collectors.groupingBy(invoker -> invoker.getModel().getPubMode()));
            if (!invokerModeMap.isEmpty()) {
                MODE_INSTANCES.putAll(invokerModeMap);
            }

            Map<String, List<Invoker<T>>> invokerHostMap = invokers.stream().collect(Collectors.groupingBy(invoker -> invoker.getModel().getHost()));
            if (!invokerHostMap.isEmpty()) {
                HOST_INSTANCES.putAll(invokerHostMap);
            }
        }

    }
}
