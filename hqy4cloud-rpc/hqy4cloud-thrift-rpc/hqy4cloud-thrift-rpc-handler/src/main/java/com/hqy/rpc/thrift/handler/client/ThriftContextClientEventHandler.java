package com.hqy.rpc.thrift.handler.client;

import com.facebook.nifty.client.ClientRequestContext;
import com.facebook.swift.service.RuntimeTApplicationException;
import com.facebook.swift.service.ThriftClientEventHandler;
import com.hqy.rpc.common.support.RPCContext;
import com.hqy.rpc.core.ThriftRequestPram;
import com.hqy.rpc.thrift.service.ThriftContextClientHandleService;
import com.hqy.rpc.thrift.support.ThriftContext;
import com.hqy.cloud.util.IpUtil;
import com.hqy.cloud.util.JsonUtil;
import org.apache.thrift.TApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;

/**
 * ThriftContextClientEventHandler for {@link ThriftClientEventHandler}
 * @see com.hqy.rpc.thrift.support.ThriftContext
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/19 17:03
 */
public class ThriftContextClientEventHandler extends ThriftClientEventHandler {

    private static final Logger log = LoggerFactory.getLogger(ThriftContextClientEventHandler.class);

    private final List<ThriftContextClientHandleService> services;

    public ThriftContextClientEventHandler(List<ThriftContextClientHandleService> services) {
        this.services = services;
    }

    @Override
    public Object getContext(String methodName, ClientRequestContext requestContext) {
        RPCContext context = RPCContext.getRpcContext();
        if (context == null) {
            String remoteAddress = requestContext.getRemoteAddress().toString();
            log.warn("Rpc context should not be null in the client side, methodName:{}, localAddress:{},  remoteAddress:{}.", methodName, IpUtil.getHostAddress(), remoteAddress);
        }
        return new ThriftContext(context);
    }

    @Override
    public void preWrite(Object context, String methodName, Object[] args) {
        if (context instanceof ThriftContext) {
            ThriftContext thriftContext = (ThriftContext) context;
            thriftContext.setPreWriteTime(System.currentTimeMillis());
            //inject ex parameters.
            checkingInjectParams(thriftContext, args);

            for (ThriftContextClientHandleService service : services) {
                try {
                    service.doPreWrite(thriftContext, methodName, args);
                } catch (Throwable t) {
                    log.error("Failed execute to doPreWrite from {}, context:{}.", service.getClass().getSimpleName(), JsonUtil.toJson(thriftContext));
                    log.error(t.getMessage(), t);
                }
            }
        }
    }

    private void checkingInjectParams(ThriftContext thriftContext, Object[] args) {
        Object obj = args[args.length - 1];
        if (obj instanceof ThriftRequestPram) {
            thriftContext.setRequestPram((ThriftRequestPram)obj);
        }
    }

    @Override
    public void postWrite(Object context, String methodName, Object[] args) {
        if (context instanceof ThriftContext) {
            ThriftContext thriftContext = (ThriftContext) context;
            thriftContext.setPostWriteTime(System.currentTimeMillis());
            for (ThriftContextClientHandleService service : services) {
                try {
                    service.doPostWrite(thriftContext, methodName, args);
                } catch (Throwable t) {
                    log.error("Failed execute to doPostWrite from {}, context:{}.", service.getClass().getSimpleName(), JsonUtil.toJson(thriftContext));
                    log.error(t.getMessage(), t);
                }
            }
        }
    }

    @Override
    public void preRead(Object context, String methodName) {
        if (context instanceof ThriftContext) {
            ThriftContext thriftContext = (ThriftContext) context;
            thriftContext.setPreReadTime(System.currentTimeMillis());
            for (ThriftContextClientHandleService service : services) {
                try {
                    service.doPreRead(thriftContext, methodName);
                } catch (Throwable t) {
                    log.error("Failed execute to doPreRead from {}, context:{}.", service.getClass().getSimpleName(), JsonUtil.toJson(thriftContext));
                    log.error(t.getMessage(), t);
                }
            }
        }
    }

    @Override
    public void postRead(Object context, String methodName, Object result) {
        if (context instanceof ThriftContext) {
            ThriftContext thriftContext = (ThriftContext) context;
            thriftContext.setPostReadTime(System.currentTimeMillis());
            for (ThriftContextClientHandleService service : services) {
                try {
                    service.doPostRead(thriftContext, methodName, result);
                } catch (Throwable t) {
                    log.error("Failed execute to doPostRead from {}, context:{}.", service.getClass().getSimpleName(), JsonUtil.toJson(thriftContext));
                    log.error(t.getMessage(), t);
                }
            }
        }
    }

    @Override
    public void preReadException(Object context, String methodName, Throwable t) {
        log.error("Throw exception from preRead, cause: {}.", t.getMessage());
        preRead(context, methodName);
        ((ThriftContext) context).setResult(false);
        compatibilityServerReturnNull(t, (ThriftContext) context);
    }

    @Override
    public void postReadException(Object context, String methodName, Throwable e) {
        log.error("Throw exception from postRead, cause:{}.", e.getMessage());
        postRead(context, methodName, null);
        if (e instanceof TApplicationException && e.getMessage() != null && e.getMessage().contains("unknown result")) {
            ((ThriftContext) context).setResult(true);
            log.warn("Rpc method result return null, methodName:{}.", methodName);
        } else {
            compatibilityServerReturnNull(e, (ThriftContext) context);
        }
    }

    @Override
    public void done(Object context, String methodName) {
        if (context instanceof ThriftContext) {
            ThriftContext thriftContext = (ThriftContext) context;
            for (ThriftContextClientHandleService service : services) {
                try {
                    service.doDone(thriftContext, methodName);
                } catch (Throwable t) {
                    log.error("Failed execute to doDone from {}, context:{}.", service.getClass().getSimpleName(), JsonUtil.toJson(thriftContext));
                    log.error(t.getMessage(), t);
                }
            }
        }
    }

    /**
     * 兼容客户端返回了null
     * @param exception        异常
     * @param context   thrift 上下文
     */
    protected void compatibilityServerReturnNull(Throwable exception, ThriftContext context) {
        if (exception instanceof InvocationTargetException && exception.getCause() != null
                && exception.getCause() instanceof RuntimeTApplicationException && exception.getCause().getCause() != null
                && exception.getCause().getCause() instanceof TApplicationException
                && ((TApplicationException) exception.getCause().getCause()).getType() == TApplicationException.MISSING_RESULT) {
            context.setResult(true);
        } else {
            context.setException(exception);
            context.setResult(false);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ThriftContextClientEventHandler that = (ThriftContextClientEventHandler) o;
        return Objects.equals(services, that.services);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), services);
    }
}
