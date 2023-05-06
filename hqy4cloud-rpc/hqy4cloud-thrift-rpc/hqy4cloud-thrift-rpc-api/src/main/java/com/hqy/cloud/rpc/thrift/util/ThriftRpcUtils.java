package com.hqy.cloud.rpc.thrift.util;

import com.facebook.swift.service.ThriftMethod;
import com.hqy.cloud.rpc.InvokeMode;

import java.lang.reflect.Method;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/11 17:39
 */
public class ThriftRpcUtils {

    public static InvokeMode getInvokeMode(Method method) {
        ThriftMethod thriftMethod = method.getAnnotation(ThriftMethod.class);
        if (thriftMethod != null) {
            if (thriftMethod.oneway()) {
                return InvokeMode.ASYNC;
            }
        }
        return InvokeMode.SYNC;

    }



}
