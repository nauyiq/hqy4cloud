package com.hqy.cloud.rpc.thrift.client.protocol;

import com.facebook.swift.service.RuntimeTApplicationException;
import com.facebook.swift.service.RuntimeTTransportException;
import com.facebook.swift.service.exception.ThriftExceptionInformation;
import com.facebook.swift.service.exception.support.ThriftCustomException;
import com.facebook.swift.service.exception.support.ThriftServerExceptionRegistry;
import com.hqy.cloud.common.base.lang.exception.NoAvailableProviderException;
import com.hqy.cloud.common.base.lang.exception.RpcException;
import com.hqy.cloud.rpc.Invocation;
import com.hqy.cloud.rpc.Invoker;
import com.hqy.cloud.rpc.fallback.Fallback;
import com.hqy.cloud.rpc.model.RpcServerAddress;
import com.hqy.cloud.rpc.model.RpcModel;
import com.hqy.cloud.rpc.protocol.AbstractInvoker;
import com.hqy.cloud.rpc.transaction.TransactionContext;
import com.hqy.cloud.rpc.thrift.client.commonpool.MultiplexThriftClientTargetPooled;
import org.apache.thrift.TApplicationException;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static com.hqy.cloud.common.base.lang.exception.RpcException.NO_PROVIDER_EXCEPTION;

/**
 * Thrift rpc for {@link Invoker}
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/6
 */
public class ThriftInvoker<T> extends AbstractInvoker<T> {
    private static final Logger log = LoggerFactory.getLogger(ThriftInvoker.class);

    private final MultiplexThriftClientTargetPooled<T> thriftClientTargetPooled;

    public ThriftInvoker(Class<T> serviceType, RpcModel rpcModel, RpcModel consumerModel, MultiplexThriftClientTargetPooled<T> thriftClientTargetPooled) throws Exception {
        this(serviceType, rpcModel, consumerModel, thriftClientTargetPooled, null);
    }

    public ThriftInvoker(Class<T> serviceType, RpcModel rpcModel, RpcModel consumerModel,
                         MultiplexThriftClientTargetPooled<T> thriftClientTargetPooled, Map<String, Object> attachment) throws Exception {
        super(serviceType, rpcModel, consumerModel, attachment);
        this.thriftClientTargetPooled = thriftClientTargetPooled;
    }

    @Override
    protected Object doInvoke(Invocation invocation) throws RpcException {
        //result obj,
        Object result = null;
        //rpcService proxy.
        T target = null;
        //need return obj to pool.
        boolean needReturnTarget = true;
        //netty channel information.
        String targetInfo = "";
        //server address
        RpcServerAddress serverAddress = getModel().getServerAddress();
        try {
            target = thriftClientTargetPooled.getTargetClient(serverAddress);
            targetInfo = thriftClientTargetPooled.gerServiceInfo(target);
            result = invocation.getMethod().invoke(target, invocation.getArguments()) ;
            if (Objects.nonNull(invocation.getInvocationCallback())) {
                invocation.getInvocationCallback().doCallback(result);
            }
        } catch (NoAvailableProviderException e) {
            needReturnTarget = false;
            doNoAvailableProviderException(invocation, e);
        } catch (ExecutionException e) {
            needReturnTarget = false;
            doExecutionException(invocation, targetInfo, e);
        } catch (Exception e) {
            try {
                result = doPreException(invocation, e);
                needReturnTarget = doException(invocation, targetInfo, e);
            } catch (RpcException rpcException) {
                throw rpcException;
            } catch (Exception exception) {
                throw new RpcException(RpcException.BIZ_EXCEPTION, exception);
            }
        } finally {
            try {
                if (needReturnTarget) {
                    thriftClientTargetPooled.returnTargetClient(serverAddress, target);
                } else {
                    if (target != null) {
                        thriftClientTargetPooled.invalidTargetClient(serverAddress, target);
                    }
                }
            } catch (Throwable t) {
                String err = String.format("ThriftInvoker return target failed, target:%s, method:%s, args:%s",
                        targetInfo, invocation.getMethod().getName(), Arrays.toString(invocation.getArguments()));
                log.warn("Failed execute for [{}], {}", err, t.getMessage(), t);
            }

        }
        return result;
    }

    private Object doPreException(Invocation invocation, Exception e) throws Exception {
        Class<? extends Exception> exceptionClass = e.getClass();
        if (exceptionClass.isAssignableFrom(ThriftCustomException.class)) {
            Fallback fallback = invocation.getFallback(exceptionClass);
            return fallback.handle(this, invocation, e);
        } else {
            if (e instanceof InvocationTargetException && ((InvocationTargetException) e).getTargetException() != null) {
                Throwable targetException = ((InvocationTargetException) e).getTargetException();
                Exception cause = (Exception) targetException.getCause();
                Class<? extends Exception> fallbackException = cause.getClass();
                Fallback fallback = null;
                if (cause instanceof TApplicationException) {
                    TApplicationException tApplicationException = (TApplicationException) cause;
                    ThriftExceptionInformation information = ThriftServerExceptionRegistry.getExceptionInformation(tApplicationException.getType());
                    if (Objects.nonNull(information)) {
                        fallback = invocation.getFallback(information.getExceptionType());
                    }
                }

                if (Objects.isNull(fallback)) {
                    fallback = invocation.getFallback(fallbackException);
                }

                if (Objects.nonNull(fallback)) {
                    return fallback.handle(this, invocation, cause);
                }
            }
        }
        return null;
    }

    private boolean doException(Invocation invocation, String targetInfo, Exception e) {
        if (checkIfDisconnectedByServer(e)) {
            log.warn("RPC found a serious error and the connection request was rejected by the long connection over the server NIO channel.");
            return false;
        }

        String err = String.format("ThriftInvoker invoke failed, target:%s, method:%s, args:%s",
                targetInfo, invocation.getMethod().getName(), Arrays.toString(invocation.getArguments()));

        if (e instanceof InvocationTargetException && e.getCause() != null
                && e.getCause() instanceof RuntimeTApplicationException
                && e.getCause().getCause() != null
                && e.getCause().getCause() instanceof TApplicationException
                && ((TApplicationException) e.getCause().getCause()).getType() == TApplicationException.MISSING_RESULT) {
            log.warn("{}, thrift rpc return null.", err);
        } else if (e instanceof TTransportException) {
            log.warn("Invoke RPC method, Legitimate Exception happen : {}, {} ", e.getClass().getName(), e.getMessage());
            throw new RpcException(RpcException.LEGITIMATE_EXCEPTION,  err + ", exception " +  e.getClass().getName(), e);
        } else {
            throw new RpcException(RpcException.UNKNOWN_EXCEPTION,  err + ", exception " +  e.getClass().getName(), e);
        }
        return true;
    }

    private void doExecutionException(Invocation invocation, String targetInfo, ExecutionException e) {
        String err = String.format("ThriftInvoker invoke failed, target:%s, method:%s, args:%s",
                targetInfo, invocation.getMethod().getName(), Arrays.toString(invocation.getArguments()));
        throw new RpcException(RpcException.TIMEOUT_EXCEPTION, err + ", ExecutionException:" + e.getMessage(), e);
    }

    private void doNoAvailableProviderException(Invocation invocation, NoAvailableProviderException e) {
        String msg = String.format("ThriftInvoker invoke failed for %s, method:%s, args:%s",
                "NoAvailableProviderException", invocation.getMethod().getName(), Arrays.toString(invocation.getArguments()));
        log.error(msg, e);
        throw new RpcException(NO_PROVIDER_EXCEPTION, msg);
    }

    /**
     * 检查连接是否断开
     * @param e 异常
     * @return boolean
     */
    private boolean checkIfDisconnectedByServer(Exception e) {
        boolean matchDisconnectedByServer = false;
        if (e instanceof InvocationTargetException) {
            String keyword = "Client was disconnected by server";
            InvocationTargetException ite = (InvocationTargetException) e;

            if (Objects.nonNull(ite.getCause())
                    && ite.getCause().getClass().equals(RuntimeTTransportException.class)) {
                //thrift RuntimeTTransportException 异常
                RuntimeTTransportException iteCause = (RuntimeTTransportException) ite.getCause();
                log.warn("@@@ RuntimeTTransportException:{}", iteCause.getMessage());
                //客户端与服务端断开连接
                if (keyword.equals(iteCause.getMessage())) {
                    //继续重试一下
                    matchDisconnectedByServer = true;
                }
            }

        }
        return matchDisconnectedByServer;
    }

    @Override
    protected void doPrepareInvoke(Invocation invocation) {
        //如果是否是分布式事务方法，则在调用之前标记一下. 在thrift rpc调用过程中会进行事务传播.
        TransactionContext.makeThriftMethodTransactional(invocation.getMethod());
    }

}
