package com.hqy.cloud.thrift.handler;

import com.facebook.nifty.core.RequestContext;
import com.facebook.swift.service.ThriftEventHandler;
import com.hqy.cloud.rpc.model.RPCModel;
import com.facebook.ThriftRequestPram;
import com.hqy.cloud.rpc.thrift.service.ThriftServerContextHandleService;
import com.hqy.cloud.rpc.thrift.support.ThriftContext;
import com.hqy.cloud.rpc.thrift.support.ThriftServerContext;
import com.hqy.cloud.thrift.core.ThriftEventHandlerUtil;
import com.hqy.cloud.util.ArgsUtil;
import com.hqy.cloud.util.JsonUtil;
import com.hqy.cloud.util.spring.ProjectContextInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * ThriftContextServerEventHandler for {@link ThriftEventHandler}
 * @see ThriftServerContext
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/21 15:37
 */
public class ThriftServerContextEventHandler extends ThriftEventHandler {
    private static final Logger log = LoggerFactory.getLogger(ThriftServerContextEventHandler.class);

    private final List<ThriftServerContextHandleService> services = new CopyOnWriteArrayList<>();

    public ThriftServerContextEventHandler(List<ThriftServerContextHandleService> services) {
        if (CollectionUtils.isNotEmpty(services)) {
            this.services.addAll(services);
        }
        ServiceLoader<ThriftServerContextHandleService> loader = ServiceLoader.load(ThriftServerContextHandleService.class);
        for (ThriftServerContextHandleService thriftServerContextHandleService : loader) {
            this.services.add(thriftServerContextHandleService);
        }
    }

    @Override
    public Object getContext(String methodName, RequestContext requestContext) {
        String communicationParty = requestContext.getConnectionContext().getRemoteAddress().toString();
        RPCModel rpcModel = ProjectContextInfo.getBean(RPCModel.class);
        if (rpcModel == null) {
            log.warn("Not found RPC model from ProjectContextInfo.");
        }
        return new ThriftServerContext(communicationParty, rpcModel);
    }

    @Override
    public void preRead(Object context, String methodName) throws TException {
        super.preRead(context, methodName);
        if (context instanceof ThriftServerContext) {
            ThriftServerContext thriftServerContext = (ThriftServerContext) context;
            thriftServerContext.setPreReadTime(System.currentTimeMillis());
            for (ThriftServerContextHandleService service : services) {
                try {
                    service.doPreRead(thriftServerContext, methodName);
                } catch (Throwable t) {
                    log.error("Failed execute to doPreRead from {}, context:{}", service.getClass().getSimpleName(), JsonUtil.toJson(thriftServerContext));
                    if (service.isThrowException()) {
                        throw t;
                    }
                }
            }
        }
    }

    @Override
    public void postRead(Object context, String methodName, Object[] args) throws TException {
        super.postRead(context, methodName, args);
        if (context instanceof ThriftServerContext) {
            ThriftServerContext thriftServerContext = (ThriftServerContext) context;
            thriftServerContext.setPostReadTime(System.currentTimeMillis());
            checkingInjectParams(thriftServerContext, args);

            for (ThriftServerContextHandleService service : services) {
                try {
                    service.doPostRead(thriftServerContext, methodName, args);
                } catch (Throwable t) {
                    log.error("Failed execute to doPostRead from {}, context:{}", service.getClass().getSimpleName(), JsonUtil.toJson(thriftServerContext));
                    if (service.isThrowException()) {
                        throw t;
                    }
                }
            }
        }
    }

    private void checkingInjectParams(ThriftServerContext thriftServerContext, Object[] args) {
        Object o = args[args.length - 1];
        if (o instanceof ThriftRequestPram) {
            thriftServerContext.setRequestPram((ThriftRequestPram)o);
        }
        //移除 ThriftMethodHandler中动态添加的参数.
        ArgsUtil.reduceTailArg(args);
    }

    @Override
    public void preWrite(Object context, String methodName, Object result) throws TException {
        super.preWrite(context, methodName, result);

        if (context instanceof ThriftServerContext) {
            ThriftServerContext thriftServerContext = (ThriftServerContext) context;
            thriftServerContext.setPreWriteTime(System.currentTimeMillis());
            for (ThriftServerContextHandleService service : services) {
                try {
                    service.doPreWrite(thriftServerContext, methodName, result);
                } catch (Throwable t) {
                    log.error("Failed execute to doPreWrite from {}, context:{}", service.getClass().getSimpleName(), JsonUtil.toJson(thriftServerContext));
                    if (service.isThrowException()) {
                        throw t;
                    }
                }
            }
        }
    }

    @Override
    public void preWriteException(Object context, String methodName, Throwable t) throws TException {
        super.preWriteException(context, methodName, t);
        String stackTrace = ExceptionUtils.getStackTrace(t);
        log.warn("Failed execute to preWrite, stackTrance: {}", stackTrace);
        ThriftEventHandlerUtil.compatibilityServerReturnNull(t, (ThriftServerContext) context);
    }

    @Override
    public void postWrite(Object context, String methodName, Object result) throws TException {
        super.postWrite(context, methodName, result);

        if (context instanceof ThriftServerContext) {
            ThriftServerContext thriftServerContext = (ThriftServerContext) context;
            thriftServerContext.setPostWriteTime(System.currentTimeMillis());
            for (ThriftServerContextHandleService service : services) {
                try {
                    service.doPostWrite(thriftServerContext, methodName, result);
                } catch (Throwable t) {
                    log.error("Failed execute to doPostWrite from {}, context:{}", service.getClass().getSimpleName(), JsonUtil.toJson(thriftServerContext));
                    if (service.isThrowException()) {
                        throw t;
                    }
                }
            }
        }
    }

    @Override
    public void postWriteException(Object context, String methodName, Throwable t) throws TException {
        super.postWriteException(context, methodName, t);
        ThriftEventHandlerUtil.compatibilityServerReturnNull(t, (ThriftServerContext) context);
    }


    @Override
    public void done(Object context, String methodName) {
        super.done(context, methodName);
        ThriftServerContext thriftServerContext = (ThriftServerContext) context;
        for (ThriftServerContextHandleService service : services) {
            try {
                service.doDone(thriftServerContext, methodName);
            } catch (Throwable t) {
                log.error("Failed execute to doDone from {}, context:{}", service.getClass().getSimpleName(), JsonUtil.toJson(thriftServerContext));
            }
        }
    }
}
