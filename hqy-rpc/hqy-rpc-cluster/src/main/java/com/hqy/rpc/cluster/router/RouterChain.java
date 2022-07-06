package com.hqy.rpc.cluster.router;

import com.hqy.rpc.api.Invocation;
import com.hqy.rpc.api.Invoker;
import com.hqy.rpc.cluster.router.gray.GrayModeRouterFactory;
import com.hqy.rpc.cluster.router.hashfactor.HashFactorRouterFactory;
import com.hqy.rpc.common.Metadata;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/30 16:30
 */
public class RouterChain<T> {

    private static final Logger log = LoggerFactory.getLogger(RouterChain.class);

    /**
     * full list of addresses from registry
     */
    private volatile List<Invoker<T>> invokers = Collections.emptyList();

    /**
     *  containing all routers
     */
    private volatile List<Router> routers = Collections.emptyList();

    /**
     * Fixed router instances: GrayModeRouter, HashFactorRouter
     * the rule for each instance may change but the instance will never delete or recreate.
     */
    private volatile List<Router> builtinRouters = Collections.emptyList();

    /**
     * Should continue route if current router's result is empty
     */
    private final boolean shouldFailFast;

    public static <T> RouterChain<T> buildChain(Class<T> interfaceClass, Metadata metadata) {
        //TODO according to interfaceClass init routers
        List<RouterFactory> routerFactories = Arrays.asList(new GrayModeRouterFactory(), new HashFactorRouterFactory());
        List<Router> routers = routerFactories.stream().map(factory -> factory.createRouter(metadata)).sorted(Router::compareTo).collect(Collectors.toList());
        //TODO according to interfaceClass init shouldFailFast.
        return new RouterChain<>(routers, true);
    }

    public static <T> RouterChain<T> buildChain(Metadata metadata, List<RouterFactory> routerFactories, boolean shouldFailFast) {
        List<Router> routers = routerFactories.stream().map(factory -> factory.createRouter(metadata)).sorted(Router::compareTo).collect(Collectors.toList());
        return new RouterChain<>(routers, shouldFailFast);
    }



    public RouterChain(List<Router> routers, boolean shouldFailFast) {
        initWithRouters(routers);
        this.shouldFailFast = shouldFailFast;
    }

    private void initWithRouters(List<Router> routers) {
        this.builtinRouters = routers;
        this.routers = new LinkedList<>(builtinRouters);
    }


    public void addRouters(List<Router> routers) {
        List<Router> newRouters = new LinkedList<>();
        newRouters.addAll(builtinRouters);
        newRouters.addAll(routers);
        newRouters = newRouters.stream().sorted(Router::compareTo).collect(Collectors.toList());
        this.routers = newRouters;
    }

    public List<Router> getRouters() {
        return routers;
    }

    public List<Invoker<T>> route(Metadata metadata, List<Invoker<T>> availableInvokers, Invocation invocation) {

        List<Invoker<T>> commonRouterResult = new ArrayList<>(availableInvokers);

        for (Router router : routers) {
            RouterResult<Invoker<T>> routerResult = router.route(commonRouterResult, metadata, invocation);
            commonRouterResult = routerResult.getResult();
            if (CollectionUtils.isEmpty(commonRouterResult) && shouldFailFast) {
                printRouterSnapshot(metadata, availableInvokers, invocation);
                return Collections.emptyList();
            }

            // stop continue routing
            if (!routerResult.isNeedContinueRoute()) {
                return commonRouterResult;
            }
        }

        if (CollectionUtils.isEmpty(commonRouterResult)) {
            return Collections.emptyList();
        }

        return commonRouterResult;
    }

    private void printRouterSnapshot(Metadata metadata, List<Invoker<T>> availableInvokers, Invocation invocation) {
        if (log.isWarnEnabled()) {

        }
    }


    /**
     * Notify router chain of the initial addresses from registry at the first time.
     * Notify whenever addresses in registry change.
     */
    public void setInvokers(List<Invoker<T>> invokers) {
        this.invokers = (invokers == null ? Collections.emptyList() : invokers);
        routers.forEach(router -> router.notify(this.invokers));
    }

    public void destroy() {
        invokers = Collections.emptyList();
        for (Router router : routers) {
            try {
                router.stop();
            } catch (Exception e) {
                log.error("Error trying to stop router " + router.getClass(), e);
            }
        }
        routers = Collections.emptyList();
        builtinRouters = Collections.emptyList();

    }


}
