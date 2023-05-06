package com.hqy.cloud.rpc;

import static com.hqy.cloud.common.base.lang.StringConstants.Symbol.*;
import static com.hqy.cloud.common.base.lang.StringConstants.Symbol.RIGHT_BRACKET;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/6 10:28
 */
public class InvocationUtil {

    public static String getMethodResourceName(Invocation invocation) {
        return getMethodResourceName(invocation.getInvoker(), invocation);
    }

    public static String getMethodResourceName(Invoker<?> invoker, Invocation invocation) {
        String interfaceResource = invoker.getInterface().getName();
        StringBuilder sb = new StringBuilder(64);
        sb.append(interfaceResource)
                .append(COLON)
                .append(invocation.getMethodName())
                .append(LEFT_BRACKET);
        boolean isFirst = true;
        for (Class<?> parameterType : invocation.getParameterTypes()) {
            if (!isFirst) {
                sb.append(COMMA);
            }
            sb.append(parameterType.getName());
            isFirst = false;
        }
        sb.append(RIGHT_BRACKET);
        return sb.toString();
    }

    public static String getInterfaceName(Invoker<?> invoker) {
        return invoker.getInterface().getName();
    }


}
