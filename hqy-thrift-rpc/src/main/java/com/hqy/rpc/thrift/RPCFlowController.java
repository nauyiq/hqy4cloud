package com.hqy.rpc.thrift;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.hqy.coll.service.CollPersistService;
import com.hqy.coll.struct.RPCMinuteFlowRecordStruct;
import com.hqy.base.common.base.lang.BaseMathConstants;
import com.hqy.base.common.queue.SimpleMessageQueue;
import com.hqy.base.common.swticher.CommonSwitcher;
import com.hqy.rpc.RPCClient;
import com.hqy.util.CommonDateUtil;
import com.hqy.util.JsonUtil;
import com.hqy.util.spring.SpringContextHolder;
import com.hqy.util.thread.DefaultThreadFactory;
import com.hqy.util.thread.ParentExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * rpc流量控制器
 * @author qiyuan.hong
 * @date 2022-03-17 17:02
 */
@Slf4j
public class RPCFlowController {

    private volatile static RPCFlowController instance = null;

    private static final Set<String> IGNORE_METHODS = new HashSet<>();

    static {
        //TODO 初始化需要忽略计数的rpc方法
    }

    private RPCFlowController() {
        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(()-> {

            final List<RPCFlowMetadata> copyQueueData = new LinkedList<>(SIMPLE_MESSAGE_QUEUE.getQueue());
            SIMPLE_MESSAGE_QUEUE.clear();
            if (CollectionUtils.isEmpty(copyQueueData)) {
                return;
            }
            //异步分析，尽量避免计算开销导致的timer 偏移 和不准...
            ParentExecutorService.getInstance().execute(() -> {
                log.info("@@@ 分析copyQueueData >>> size:{}", copyQueueData.size());

                if (CommonSwitcher.ENABLE_THRIFT_RPC_COLLECT.isOn()) {
                    RPCFlowMinutedInfo rpcFlowMinutedInfo = analyze(copyQueueData);
                    log.info("@@@ 分析copyQueueData, rpcFlowMinutedInfo = {}", JsonUtil.toJson(rpcFlowMinutedInfo));
                    //采集服务 RPC入库
                    CollPersistService persistService = RPCClient.getRemoteService(CollPersistService.class);
                    persistService.saveRpcMinuteFlowRecord(new RPCMinuteFlowRecordStruct(rpcFlowMinutedInfo.getCaller(),
                            rpcFlowMinutedInfo.getTotal(), rpcFlowMinutedInfo.getNgTotal(), rpcFlowMinutedInfo.getWindow(),
                            JsonUtil.toJson(rpcFlowMinutedInfo.serviceMap), JsonUtil.toJson(rpcFlowMinutedInfo.methodMap)));
                }
            });

        }, BaseMathConstants.ONE_MINUTES_4MILLISECONDS * 60, BaseMathConstants.ONE_MINUTES_4MILLISECONDS, TimeUnit.MILLISECONDS);
    }

    private RPCFlowMinutedInfo analyze(List<RPCFlowMetadata> copyQueueData) {
        //获取当前调用者信息
        String caller = SpringContextHolder.getProjectContextInfo().getNameWithIpPort();
        //时间窗口 由于定期任务是一分钟跑一次 因此往前偏移一分钟
        DateTime offsetMinute = DateUtil.offsetMinute(new Date(), -1);
        String timeWindows = DateUtil.format(offsetMinute, CommonDateUtil.TIME_MINUTE_FORMAT);
        RPCFlowMinutedInfo flow = new RPCFlowMinutedInfo(caller, timeWindows);

        for (RPCFlowMetadata metadata : copyQueueData) {
            //method分组下的rpc计数
            String methodName = metadata.getMethodName();
            if (flow.methodMap.containsKey(methodName)) {
                Integer count = flow.methodMap.get(methodName);
                flow.methodMap.put(methodName, count + 1);
            } else {
                flow.methodMap.put(methodName, 1);
            }
            //service分组下的rpc计数
            String serviceName = metadata.getService().getSimpleName();
            if (flow.serviceMap.containsKey(serviceName)) {
                Integer count = flow.serviceMap.get(serviceName);
                flow.serviceMap.put(serviceName, count + 1);
            } else {
                flow.serviceMap.put(serviceName, 1);
            }

            Boolean rpcResult = metadata.getRpcResult();
            if (!rpcResult) {
                flow.ngTotal++;
            }
            flow.total++;
        }

        return flow;
    }

    public static RPCFlowController getInstance() {
        if (Objects.isNull(instance)) {
            synchronized (RPCFlowController.class) {
                if (Objects.isNull(instance)) {
                    instance = new RPCFlowController();
                }
            }
        }
        return instance;
    }

    private static final SimpleMessageQueue<RPCFlowMetadata> SIMPLE_MESSAGE_QUEUE = new SimpleMessageQueue<>(1024 * 8);


    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE
            = new ScheduledThreadPoolExecutor(1, new DefaultThreadFactory("RPCFlowController"));




    /**
     * 记录一次rpc调用.
     * @param methodName 方法名
     * @param service rpc service
     * @param rpcSuccess 是否成功
     */
    public void count(String methodName, Class<?> service, boolean rpcSuccess) {
        if (StringUtils.isBlank(methodName) || Objects.isNull(service)) {
            return;
        }
        if (IGNORE_METHODS.contains(methodName)) {
            return;
        }

        if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
            log.info("@@@ RPCFlowController, 记录一次RPC调用. methName:{}, class:{}, success:{}", methodName, service.getSimpleName(), rpcSuccess);
        }

        //放进队列 用定时任务及时处理.
        SIMPLE_MESSAGE_QUEUE.addTail(new RPCFlowMetadata(methodName, service, rpcSuccess));
    }



}
