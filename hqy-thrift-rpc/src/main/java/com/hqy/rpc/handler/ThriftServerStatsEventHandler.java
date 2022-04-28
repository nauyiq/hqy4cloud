package com.hqy.rpc.handler;

import com.facebook.nifty.core.RequestContext;
import com.facebook.swift.service.ThriftEventHandler;
import com.hqy.base.common.swticher.CommonSwitcher;
import com.hqy.rpc.thrift.RemoteExParam;
import com.hqy.rpc.thrift.RequestContextKey;
import com.hqy.rpc.thrift.ThriftContext;
import com.hqy.rpc.transaction.TransactionContext;
import com.hqy.util.ArgsUtil;
import com.hqy.util.JsonUtil;
import io.seata.core.context.RootContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * thrift服务端事件handler. 声明后 可以动态的在thrift rpc的执行生命周期内 拓展业务. 类似aop
 * 绑定RemoteExParam拓展参数：
 *            1 标记当前rpc责任链.
 *            2 绑定seata分布式事务全局事务id.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/22 15:32
 */
public class ThriftServerStatsEventHandler extends ThriftEventHandler {

    private static final Logger log = LoggerFactory.getLogger(ThriftServerStatsEventHandler.class);

    /**
     * thrift rpc请求中的上下文
     */
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
            getInjectTransactionXidAndBinding(remoteExParam, methodName);
        } catch (Exception e) {
            log.warn("@@@ ThriftServerStatsEventHandler.postRead error. [{}]", e.getMessage());
        }
    }

    private void getInjectTransactionXidAndBinding(RemoteExParam remoteExParam, String methodName) {
        if (CommonSwitcher.ENABLE_PROPAGATE_GLOBAL_TRANSACTION.isOn()) {
            String rpcXid = remoteExParam.xid;
            String xid = RootContext.getXID();
            if (StringUtils.isBlank(xid) && StringUtils.isNotBlank(rpcXid) && TransactionContext.isTransactional(methodName)) {
                try {
                    RootContext.bind(rpcXid);
                    log.info("@@@ GlobalTransactional rpcXid binding. rpcXid:{}, method:{}", rpcXid, methodName);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            } else {
                log.warn("@@@ Checking method : {} need binding rpcXid, but rpcXid is empty. exParam:{}", methodName, JsonUtil.toJson(remoteExParam));
            }
        } else {
            log.info("@@@ CommonSwitcher.ENABLE_PROPAGATE_GLOBAL_TRANSACTION.isOff");
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
        setPropertiesByException((ThriftContext) context, t, stackTrace);
    }



    @Override
    public void postWriteException(Object context, String methodName, Throwable t) throws TException {
        ((ThriftContext)context).setResult(false);

        if (((ThriftContext)context).getThrowable() == null) {
            String stackTrace = ExceptionUtils.getStackTrace(t);
            setPropertiesByException((ThriftContext) context, t, stackTrace);
        }
    }

    @Override
    public void done(Object context, String methodName) {
        ThriftContext thriftContext = (ThriftContext) context;
        try {
            if (CommonSwitcher.ENABLE_THRIFT_RPC_COLLECTION.isOn()) {
                /*String rid = thriftContext.getRootId();
                String cid = thriftContext.getChildId();
                String pid =  thriftContext.getParentId() ;
                if(StringUtils.isNotEmpty(rid)) {
                    rootId = Long.parseLong(rid);

                }
                if(StringUtils.isNotEmpty(cid)) {
                    childId = Long.parseLong(cid);
                }
                if(StringUtils.isNotEmpty(pid)) {
                   Long  parentId = Long.parseLong(pid);
                    ThreadLocalCCC.PARENT_ID.set(parentId);
                }*/
            }
        } catch (Exception e) {

        }

        //TODO 异常采集
        super.done(context, methodName);
    }

    private void setPropertiesByException(ThriftContext context, Throwable t, String stackTrace) {
        context.setThrowable(t);
        requestContext.getConnectionContext().setAttribute(RequestContextKey.KEY_RPC_SUCCESS, Boolean.FALSE);
        requestContext.getConnectionContext().setAttribute(RequestContextKey.KEY_EXCEPTION_STACK, stackTrace);
        requestContext.getConnectionContext().setAttribute(RequestContextKey.KEY_EXCEPTION_CLASS, t.getClass().getName());
        requestContext.setContextData(RequestContextKey.KEY_RPC_SUCCESS, Boolean.FALSE);
        requestContext.setContextData(RequestContextKey.KEY_EXCEPTION_STACK, stackTrace);
        requestContext.setContextData(RequestContextKey.KEY_EXCEPTION_CLASS, t.getClass().getName());
    }
}
