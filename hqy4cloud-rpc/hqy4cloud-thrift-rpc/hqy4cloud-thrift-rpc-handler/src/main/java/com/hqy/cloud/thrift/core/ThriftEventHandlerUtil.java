package com.hqy.cloud.thrift.core;

import com.facebook.swift.service.RuntimeTApplicationException;
import com.hqy.cloud.rpc.thrift.support.Context;
import org.apache.thrift.TApplicationException;

import java.lang.reflect.InvocationTargetException;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/6 14:35
 */
public class ThriftEventHandlerUtil {

    private ThriftEventHandlerUtil() {}

    /**
     * 兼容客户端返回了null
     * @param exception        异常
     * @param context   thrift 上下文
     */
    public static void compatibilityServerReturnNull(Throwable exception, Context context) {
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


}
