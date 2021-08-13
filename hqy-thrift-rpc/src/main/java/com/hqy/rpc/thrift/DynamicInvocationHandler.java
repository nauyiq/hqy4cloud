package com.hqy.rpc.thrift;

import com.hqy.rpc.route.AbstractRPCRouter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 每个RPCService 接口对应一个Handler， 可以接受nacos的事件通知
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-13 9:56
 */
@Slf4j
public class DynamicInvocationHandler<T> extends AbstractRPCRouter implements InvocationHandler {


    private static InvokeCallback callback;

//    private




    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }
}
