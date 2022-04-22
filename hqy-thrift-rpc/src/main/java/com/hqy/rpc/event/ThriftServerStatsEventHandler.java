package com.hqy.rpc.event;

import com.facebook.nifty.core.RequestContext;
import com.facebook.swift.service.ThriftEventHandler;
import com.hqy.rpc.thrift.RemoteExParam;
import com.hqy.rpc.thrift.RequestContextKey;
import com.hqy.rpc.thrift.ThriftContext;
import com.hqy.util.ArgsUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * thrift服务端事件handler.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/22 15:32
 */
public class ThriftServerStatsEventHandler extends ThriftEventHandler {

    private static final Logger log = LoggerFactory.getLogger(ThriftServerStatsEventHandler.class);

    private RequestContext requestContext = null;

    @Override
    public Object getContext(String methodName, RequestContext requestContext) {
        this.requestContext = requestContext;
        String remoteAddress = requestContext.getConnectionContext().getRemoteAddress().toString();
        return new ThriftContext(remoteAddress);
    }


    @Override
    public void preRead(Object context, String methodName) throws TException {
        ((ThriftContext)context).setPreReadTime(System.currentTimeMillis());
    }

    @Override
    public void postRead(Object context, String methodName, Object[] args) throws TException {
        ((ThriftContext)context).setPostReadTime(System.currentTimeMillis());
        try {
            //读取动态的拓展的参数.
            RemoteExParam remoteExParam = (RemoteExParam) args[args.length - 1];
            ((ThriftContext)context).setParam(remoteExParam);
            //移除 ThriftMethodHandler中动态添加的参数.
            ArgsUtil.reduceTailArg(args);
        } catch (Exception e) {
            log.warn("@@@ ThriftServerStatsEventHandler.postRead error. [{}]", e.getMessage());
        }
    }

    @Override
    public void preWrite(Object context, String methodName, Object result) throws TException {
        ((ThriftContext)context).setPreWriteTime(System.currentTimeMillis());
    }

    @Override
    public void postWrite(Object context, String methodName, Object result) throws TException {
        ((ThriftContext)context).setPostWriteTime(System.currentTimeMillis());
    }

    @Override
    public void preWriteException(Object context, String methodName, Throwable t) throws TException {
        String stackTrace = ExceptionUtils.getStackTrace(t);
        log.warn("@@@ preWriteException. stackTrance: {}", stackTrace);
        ((ThriftContext)context).setResult(false);
        ((ThriftContext)context).setThrowable(t);

        requestContext.getConnectionContext().setAttribute(RequestContextKey.KEY_RPC_SUCCESS, Boolean.FALSE);
        requestContext.getConnectionContext().setAttribute(RequestContextKey.KEY_EXCEPTION_STACK, stackTrace);
        requestContext.getConnectionContext().setAttribute(RequestContextKey.KEY_EXCEPTION_CLASS, t.getClass().getName());
        requestContext.setContextData(RequestContextKey.KEY_RPC_SUCCESS, Boolean.FALSE);
        requestContext.setContextData(RequestContextKey.KEY_EXCEPTION_STACK, stackTrace);
        requestContext.setContextData(RequestContextKey.KEY_EXCEPTION_CLASS, t.getClass().getName());
    }

    @Override
    public void postWriteException(Object context, String methodName, Throwable t) throws TException {
        ((ThriftContext)context).setResult(false);

        if (((ThriftContext)context).getThrowable() == null) {
            String stackTrace = ExceptionUtils.getStackTrace(t);
            ((ThriftContext)context).setThrowable(t);
            requestContext.getConnectionContext().setAttribute(RequestContextKey.KEY_RPC_SUCCESS, Boolean.FALSE);
            requestContext.getConnectionContext().setAttribute(RequestContextKey.KEY_EXCEPTION_STACK, stackTrace);
            requestContext.getConnectionContext().setAttribute(RequestContextKey.KEY_EXCEPTION_CLASS, t.getClass().getName());
            requestContext.setContextData(RequestContextKey.KEY_RPC_SUCCESS, Boolean.FALSE);
            requestContext.setContextData(RequestContextKey.KEY_EXCEPTION_STACK, stackTrace);
            requestContext.setContextData(RequestContextKey.KEY_EXCEPTION_CLASS, t.getClass().getName());
        }
    }

    @Override
    public void done(Object context, String methodName) {
        //TODO 异常采集
        super.done(context, methodName);
    }
}
