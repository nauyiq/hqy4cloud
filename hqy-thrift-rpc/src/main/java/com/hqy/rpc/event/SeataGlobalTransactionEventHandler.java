package com.hqy.rpc.event;

import com.facebook.nifty.core.RequestContext;
import com.facebook.swift.service.ThriftEventHandler;

import java.util.concurrent.Callable;

/**
 * 解决rpc调用过程Seata分布式事务传播的问题
 * 解决方案：
 * 1.远程服务调用方：
 *      发起远程服务调用时，需要把全局事务XID包含到请求信息中
 * 2.远程服务提供方：
 *      处理请求前，解析获取 XID 并绑定到 RootContext 中
 *      处理请求后，将 XID 从 RootContext 中解绑
 *
 * Seata事务管理的原理：
 * Seata 的事务上下文由 RootContext 来管理。
 * 应用开启一个全局事务后，RootContext 会自动绑定该事务的 XID，事务结束（提交或回滚完成），RootContext 会自动解绑 XID。<br/>
 * 应用可以通过 RootContext 的 API 接口（RootContext.getXID()）来获取当前运行时的全局事务 XID <br/>
 * 应用是否运行在一个全局事务的上下文中，就是通过 RootContext 是否绑定 XID 来判定的 <br/>
 * seata分布式事务在微服务框架的实现中 主要依赖于全局事务id的传播，并且需要把这个全局事务ID在各个微服务间传播。
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/21 16:51
 */
public class SeataGlobalTransactionEventHandler extends ThriftEventHandler {

    private RequestContext requestContextPoint = null;

    @Override
    public Object getContext(String methodName, RequestContext requestContext) {
        return super.getContext(methodName, requestContext);
    }




}
