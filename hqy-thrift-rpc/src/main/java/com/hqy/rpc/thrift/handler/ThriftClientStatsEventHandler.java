package com.hqy.rpc.thrift.handler;

import com.facebook.nifty.client.ClientRequestContext;
import com.facebook.nifty.client.RequestChannel;
import com.facebook.swift.service.RuntimeTApplicationException;
import com.facebook.swift.service.ThriftClientEventHandler;
import com.hqy.base.common.swticher.CommonSwitcher;
import com.hqy.rpc.regist.EnvironmentConfig;
import com.hqy.rpc.thrift.RemoteExParam;
import com.hqy.rpc.thrift.ThriftContext;
import com.hqy.rpc.thrift.ex.RemoteContextChecker;
import com.hqy.rpc.transaction.TransactionContext;
import com.hqy.util.JsonUtil;
import io.seata.core.context.RootContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/24 16:44
 */
public class ThriftClientStatsEventHandler extends ThriftClientEventHandler {

    private static final Logger log = LoggerFactory.getLogger(ThriftClientStatsEventHandler.class);

    @Override
    public Object getContext(String methodName, ClientRequestContext requestContext) {
        String remoteAddress = requestContext.getRemoteAddress().toString();
        RequestChannel requestChannel = requestContext.getRequestChannel();
        int channelHashCode = requestChannel.hashCode();
        String context = String.format("channel[%s(%s)]", remoteAddress, channelHashCode);

        if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
            log.debug("[ThriftClientStatsEventHandler.getContext] context={}", context);
        }
        return new ThriftContext(context);
    }

    @Override
    public void preWrite(Object context, final String methodName, Object[] args) {
        if (context instanceof ThriftContext) {
            boolean needCollect = RemoteContextChecker.needCollect(methodName);
            ((ThriftContext) context).setCollection(needCollect);
            ((ThriftContext) context).setPreWriteTime(System.currentTimeMillis());
            if (EnvironmentConfig.getInstance().isRPCCallChainPersistence()) {
                try {
                    //框架层面动态注入一个参数 RemoteExParam
                    Object obj = args[args.length - 1];
                    if (obj instanceof RemoteExParam) {
                        RemoteExParam param = (RemoteExParam) obj;
                        //判断是否需要注入seata xid
                        if (TransactionContext.isTransactional(methodName)) {
                            injectTransactionalXid(methodName, param);
                        }
                        ((ThriftContext) context).setParam(param);
                    } else {
                        log.warn("@@@ ThriftClientStatsEventHandler.preWrite -> not expected argument type for 'RemoteExParam':{}", obj.getClass().getSimpleName());
                    }
                    ((ThriftContext) context).setReqParamJson(JsonUtil.toJson(args));
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }

            }
        }

    }


    @Override
    public void postWrite(Object context, String methodName, Object[] args) {
        ((ThriftContext) context).setPostWriteTime(System.currentTimeMillis());
    }

    @Override
    public void preRead(Object context, String methodName) {
        ((ThriftContext) context).setPreReadTime(System.currentTimeMillis());
    }

    @Override
    public void postRead(Object context, String methodName, Object result) {
        ((ThriftContext) context).setPostReadTime(System.currentTimeMillis());
    }

    @Override
    public void preReadException(Object context, String methodName, Throwable e) {
        log.error("@@@ RPC Client preReadException:{}", e.getMessage());
        preRead(context, methodName);
        ((ThriftContext) context).setResult(false);
        //兼容客户端返回了null
        compatibilityServerReturnNull(e, (ThriftContext) context);
    }


    @Override
    public void postReadException(Object context, String methodName, Throwable e) {
        log.error("@@@ RPC@@@ RPC Client postReadException:{}, {}", e.getClass().getName(), e.getMessage());
        postRead(context, methodName, null);
        ((ThriftContext) context).setResult(false);

        if (e instanceof TApplicationException && e.getMessage() != null && e.getMessage().contains("unknown result")) {
            ((ThriftContext) context).setResult(true);
            log.warn("@@@ RPC method return null, ignored : methodName= {}", methodName);
        } else {
            //兼容客户端返回了null
            compatibilityServerReturnNull(e, (ThriftContext) context);
        }
    }

    @Override
    public void done(Object context, String methodName) {
        super.done(context, methodName);
    }


    /**
     * 兼容客户端返回了null
     * @param e
     * @param context
     */
    private void compatibilityServerReturnNull(Throwable e, ThriftContext context) {
        if (e instanceof InvocationTargetException && e.getCause() != null
                && e.getCause() instanceof RuntimeTApplicationException && e.getCause().getCause() != null
                && e.getCause().getCause() instanceof TApplicationException
                && ((TApplicationException) e.getCause().getCause()).getType() == TApplicationException.MISSING_RESULT) {
            context.setResult(true);
        } else {
            context.setThrowable(e);
        }
    }

    /**
     * 根据环境还有方法 判断是否需要注入全局事务id
     * @param methodName 方法名
     * @param param      拓展参数RemoteExParam
     */
    private void injectTransactionalXid(String methodName, RemoteExParam param) {
        if (CommonSwitcher.ENABLE_PROPAGATE_GLOBAL_TRANSACTION.isOn() ) {
            String xid = RootContext.getXID();
            if (StringUtils.isBlank(xid)) {
                log.info("@@@ Seata transaction id is empty. please check @GlobalTransactional, methodName:{}", methodName);
            }
            param.xid = xid;
        } else {
            log.info("@@@ Current environment not support distributed transactional. methodName:{}.", methodName);
        }
    }
}
