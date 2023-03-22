package com.hqy.cloud.rpc.monitor.thrift;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.rpc.cluster.client.Client;
import com.hqy.cloud.rpc.CommonConstants;
import com.hqy.cloud.rpc.model.RPCModel;
import com.hqy.cloud.rpc.threadpool.FrameworkExecutorRepository;
import com.hqy.cloud.rpc.monitor.CollectionData;
import com.hqy.cloud.rpc.monitor.Monitor;
import com.hqy.cloud.rpc.monitor.thrift.service.ThriftMonitorService;
import com.hqy.cloud.rpc.thrift.struct.ThriftRpcExceptionStruct;
import com.hqy.cloud.rpc.thrift.struct.ThriftRpcFlowStruct;
import com.hqy.cloud.util.JsonUtil;
import com.hqy.cloud.util.spring.ProjectContextInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.hqy.cloud.rpc.CommonConstants.*;

/**
 * ThriftMonitor.
 * @see ThriftMonitorService
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/18 11:08
 */
public class ThriftMonitor implements Monitor {

    private static final Logger log = LoggerFactory.getLogger(ThriftMonitor.class);

    private final Client client;

    private final ExecutorService exceptionExecutorService;

    private final RPCModel rpcModel;

    private final ScheduledFuture<?> sendFuture;

    private final Map<Statistics, AtomicReference<StatisticsFlowItem>> statisticsMap = MapUtil.newConcurrentHashMap();

    private final Map<String, AtomicReference<Integer>> statisticsExceptionMap = MapUtil.newConcurrentHashMap();

    private final long monitorInterval;

    private final int maxWarn;

    public ThriftMonitor(Client client, RPCModel rpcModel) {
        this.client = client;
        this.rpcModel = rpcModel;
        ScheduledExecutorService executorService = FrameworkExecutorRepository.getInstance().getSharedScheduledExecutor();
        this.exceptionExecutorService = FrameworkExecutorRepository.getInstance().getSharedExecutor();
        this.maxWarn = rpcModel.getParameter(RPC_MONITOR_WARNING_KEY, DEFAULT_MONITOR_WARNING_KEY);
        // The time interval for timer <b>scheduledExecutorService</b> to send data
        this.monitorInterval = rpcModel.getParameter(MONITOR_SEND_DATA_INTERVAL_KEY, DEFAULT_MONITOR_SEND_DATA_INTERVAL);
        // collect timer for collecting statistics data
        this.sendFuture = executorService.scheduleWithFixedDelay(() -> {
            try {
                // collect data
                send();
            } catch (Throwable t) {
                log.error("Unexpected error occur at send statistic, cause: " + t.getMessage(), t);
            } finally {
                //reset collection map.
                statisticsMap.clear();
            }
        }, monitorInterval, monitorInterval, TimeUnit.MILLISECONDS);
    }

    private void send() {
        if (log.isDebugEnabled()) {
            log.debug("Send statics flow struct to thrift monitor.");
        }
        List<ThriftRpcFlowStruct> structs = new ArrayList<>(statisticsMap.size());
        for (Map.Entry<Statistics, AtomicReference<StatisticsFlowItem>> entry : statisticsMap.entrySet()) {
            Statistics statistics = entry.getKey();
            AtomicReference<StatisticsFlowItem> reference = entry.getValue();
            StatisticsFlowItem statisticsFlowItem = reference.get();
            structs.add(new ThriftRpcFlowStruct(statistics.getCaller(),
                    statistics.getProvider(),
                    statisticsFlowItem.getSuccess(),
                    statisticsFlowItem.getFailure(),
                    statisticsFlowItem.getTotal(),
                    statisticsFlowItem.getInterval(),
                    JsonUtil.toJson(statisticsFlowItem.getServiceDetail()),
                    JsonUtil.toJson(statisticsFlowItem.getMethodDetail())));

            // reset
            StatisticsFlowItem current;
            StatisticsFlowItem update = new StatisticsFlowItem();

            do {
                current = reference.get();
                if (current == null) {
                    update.setItem(0, 0, 0, monitorInterval, MapUtil.newHashMap(), MapUtil.newHashMap());
                } else {
                    update.setItem(
                            current.getSuccess() - statisticsFlowItem.getSuccess(),
                            current.getFailure() - statisticsFlowItem.getFailure(),
                            current.getTotal() - statisticsFlowItem.getTotal(),
                            current.getInterval(),
                            MapUtil.newHashMap(),
                            MapUtil.newHashMap()
                    );
                }

            } while (!reference.compareAndSet(current, update));
        }
        //send to collect
        if (structs.isEmpty()) {
            return;
        }
        ThriftMonitorService monitorService = client.getApplicationService(ThriftMonitorService.class, rpcModel.getName());
        monitorService.collectRpcFlowList(structs);
    }


    @Override
    public void collect(CollectionData data) {
        if (data == null) {
            log.warn("CollectionData should not be null.");
            return;
        }

        boolean result = data.isRpcResult();
        String name = data.getServiceClass().getName();
        String method = data.getMethod();
        int success = result ? 1 : 0;
        int failure = result ? 0 : 1;

        Statistics statistics = new Statistics(data);
        // init atomic reference
        AtomicReference<StatisticsFlowItem> reference = statisticsMap.computeIfAbsent(statistics, k -> new AtomicReference<>());
        // use CompareAndSet to sum
        StatisticsFlowItem current;
        StatisticsFlowItem update = new StatisticsFlowItem();
        do {
            current = reference.get();
            if (current == null) {
                Map<String, Integer> methodMap = MapUtil.builder(new HashMap<String, Integer>(4)).put(method, 1).build();
                Map<String, Integer> serviceMap = MapUtil.builder(new HashMap<String, Integer>(4)).put(name, 1).build();
                update.setItem(success, failure, 1, monitorInterval, methodMap, serviceMap);
            } else {
                Map<String, Integer> methodDetail = current.getMethodDetail();
                Map<String, Integer> serviceDetail = current.getServiceDetail();
                Integer methodCount = methodDetail.getOrDefault(method, 0) + 1;
                Integer serviceCount = serviceDetail.getOrDefault(name, 0) + 1;
                methodDetail.put(method, methodCount);
                serviceDetail.put(name, serviceCount);
                update.setItem(
                        current.getSuccess() + success,
                        current.getFailure() + failure,
                        current.getTotal() + 1,
                             current.getInterval(),
                             methodDetail,
                             serviceDetail
                );
            }

        } while (!reference.compareAndSet(current, update));

        //if slow rpc or error rpc exist, do collect.
        checkRpcAndCollectErrorRpc(data);
    }


    private void checkRpcAndCollectErrorRpc(CollectionData data) {
        String rpcEventType = getRpcEventType(data);
        if (rpcEventType.equalsIgnoreCase(SLOW_RPC) || rpcEventType.equalsIgnoreCase(ERROR_RPC)) {
            //need collect.
            String key = data.getMethod().concat(StringConstants.Symbol.COLON).concat(rpcEventType);
            AtomicReference<Integer> reference = statisticsExceptionMap.computeIfAbsent(key, k -> new AtomicReference<>(0));
            Integer errorCount = reference.get();
            int updateCount = errorCount + 1;
            do {
                exceptionExecutorService.submit(() -> {
                    //collect exception rpc.
                    ThriftMonitorService monitorService = client.getApplicationService(ThriftMonitorService.class, rpcModel.getName());
                    monitorService.collectRpcException(new ThriftRpcExceptionStruct(rpcEventType,
                             data.getStartMillis(),
                             data.getCaller(),
                             data.getServiceClass().getName(),
                             data.getMethod(),
                             data.getCostMillis(),
                             data.getException().getMessage()));
                    //Sampling warning to notifier.
                    if (updateCount % maxWarn == 0) {
                        // TODO notify.
                    }
                });
            } while (!reference.compareAndSet(errorCount, updateCount));
        }
    }


    private String getRpcEventType(CollectionData data) {
        String eventType = data.isRpcResult() ? StringConstants.EMPTY : ERROR_RPC;
        if (StringUtils.isBlank(eventType)) {
            long slowRpcTimeMillis = CommonConstants.DEFAULT_RPC_SLOW_TIME_MILLIS;
            RPCModel rpcModel = ProjectContextInfo.getBean(RPCModel.class);
            if (rpcModel != null) {
                slowRpcTimeMillis = rpcModel.getParameter(RPC_SLOW_TIME_KEY, slowRpcTimeMillis);
            }
            eventType = data.getCostMillis() > slowRpcTimeMillis ? SLOW_RPC : NORMAL_RPC;
        }
        return eventType;
    }


}
