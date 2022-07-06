package com.hqy.rpc.thrift.ex;

import java.lang.reflect.Method;

/**
 * 泛型rpc辅助接口
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/2/22 10:53
 */
public interface GenericRpcHelper {

    /**
     * 获取模块名称
     * @param declaringClass
     * @return
     */
    String getModuleName(Class<?> declaringClass);

    /**
     * 通过消息通道来触发（泛型的）RPC方法
     * @param module
     * @param method
     * @param args
     * @return
     */
    Object invokeByMessageChannel(String module, Method method, Object[] args) ;

}
