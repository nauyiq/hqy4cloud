package com.hqy.rpc;

/**
 *
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/29 11:20
 */
public interface Invocation {

    /**
     * get method name.
     * @return method name
     */
    String getMethodName();

    /**
     * get the rpc service interface name.
     * @return  service interface name
     */
    String getServiceName();


    /**
     * get the rpc service method parameter types.
     * @return parameter types.
     * @serial
     */
    Class<?>[] getParameterTypes();

    /**
     * get arguments.
     * @return arguments.
     * @serial
     */
    Object[] getArguments();


    InvokerCallback getInvokerCallback();

}
