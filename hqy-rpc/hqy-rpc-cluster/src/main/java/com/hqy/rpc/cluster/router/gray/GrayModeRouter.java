package com.hqy.rpc.cluster.router.gray;

import com.hqy.base.common.swticher.CommonSwitcher;
import com.hqy.rpc.api.Invocation;
import com.hqy.rpc.api.Invoker;
import com.hqy.rpc.cluster.router.AbstractRouter;
import com.hqy.rpc.cluster.router.RouterResult;
import com.hqy.rpc.common.GrayWhitePub;
import com.hqy.rpc.common.Metadata;
import com.hqy.util.IpUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/30 17:41
 */
public class GrayModeRouter extends AbstractRouter {

    private static final Logger log = LoggerFactory.getLogger(GrayModeRouter.class);

    private static transient final String GRAY_PRIORITY_KEY = "gray-mode-priority";
    private static transient final int DEFAULT_PRIORITY = 2;
    private final boolean force;

    public GrayModeRouter(Metadata metadata) {
        this.metadata = metadata;
        this.priority = metadata.getParameter(GRAY_PRIORITY_KEY, DEFAULT_PRIORITY);
        this.force = Boolean.parseBoolean(metadata.getParameter(FORCE_KEY, "false"));
    }

    @Override
    public <T> RouterResult<Invoker<T>> route(List<Invoker<T>> invokers, Metadata metadata, Invocation invocation) {
        if (CollectionUtils.isEmpty(invokers) || metadata == null) {
            return new RouterResult<>(invokers, false);
        }
        boolean colorModeRpcRemote = CommonSwitcher.ENABLE_GRAY_MECHANISM.isOn();
        boolean sameIpHighPriority = CommonSwitcher.ENABLE_RPC_SAME_IP_HIGH_PRIORITY.isOn();
        if (log.isDebugEnabled()) {
            log.debug("GrayModeRouter -> Switcher 200 {} | 207 {}, ", colorModeRpcRemote, sameIpHighPriority);
        }
        if (!colorModeRpcRemote && !sameIpHighPriority) {
            //This rule invalid, continue next router.
            return new RouterResult<>(invokers, true);
        }

        try {
            List<Invoker<T>> chooseInvokers = findSuitableInvokers(colorModeRpcRemote, sameIpHighPriority, metadata, invokers);
            if (CollectionUtils.isNotEmpty(chooseInvokers)) {
                return new RouterResult<>(chooseInvokers, true);
            } else if (force) {
                log.warn("The route result is empty and force execute. consumer: " + IpUtil.getHostAddress() + ", service: " + invocation.getServiceName());
                new RouterResult<>(chooseInvokers, true);
            }
        } catch (Throwable t) {
            log.error("Failed to execute GrayMode router rule: " + getMetadata() + ", invokers: " + invokers + ", cause: " + t.getMessage(), t);
        }

        return new RouterResult<>(invokers, true);
    }

    private <T> List<Invoker<T>> findSuitableInvokers(boolean colorModeRpcRemote, boolean sameIpHighPriority, Metadata consumerMetadata, List<Invoker<T>> invokers) {
        List<Invoker<T>> chooseInvokers = new ArrayList<>();
        if (colorModeRpcRemote) {
            chooseInvokers = findGaryModeInvokers(consumerMetadata, invokers);
            if (CollectionUtils.isEmpty(chooseInvokers)) {
                return chooseInvokers;
            }
        }
        if (sameIpHighPriority) {
            List<Invoker<T>> sameIpInvokers = findSameIpInvokers(consumerMetadata, chooseInvokers);
            if (CollectionUtils.isNotEmpty(sameIpInvokers)) {
                chooseInvokers = sameIpInvokers;
            }
        }
        return chooseInvokers;
    }

    private <T> List<Invoker<T>> findGaryModeInvokers(Metadata consumerMetadata, List<Invoker<T>> invokers) {
        try {
            int consumerPubModeValue = consumerMetadata.getNode().getPubMode();
            // white server just remote white server.
            // gray server can remote any color server.
            if (consumerPubModeValue == GrayWhitePub.WHITE.value) {
                invokers = invokers.stream().filter(invoker -> {
                    int invokerPubModeValue = invoker.getMetadata().getNode().getPubMode();
                    return invokerPubModeValue != GrayWhitePub.GRAY.value;
                }).collect(Collectors.toList());
            }
        } catch (Throwable t) {
            log.warn("Failed to execute gray mode router rule, invokers: {}", invokers);
        }
        return invokers;
    }

    private <T> List<Invoker<T>> findSameIpInvokers(Metadata consumerMetadata, List<Invoker<T>> invokers) {
        try {
            String ip = consumerMetadata.getHost();
            return invokers.stream().filter(invoker -> {
                String invokerHost = invoker.getMetadata().getHost();
                return ip.equals(invokerHost);
            }).collect(Collectors.toList());
        } catch (Throwable t) {
            log.warn("Failed to execute same ip router rule, invokers: {}", invokers);
            return invokers;
        }
    }
}
