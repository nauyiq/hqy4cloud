package com.hqy.order.service;

import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/29 16:13
 */
@LocalTCC
public interface TccOderService {

    /**
     * 定义两阶段提交
     * name = 该tcc的bean名称,全局唯一
     * commitMethod = commit 为二阶段确认方法
     * rollbackMethod = rollback 为二阶段取消方法
     * BusinessActionContextParameter注解 传递参数到二阶段中
     * @param storageId
     * @param count
     * @return
     */
    @TwoPhaseBusinessAction(name = "order", commitMethod = "commitTcc", rollbackMethod = "cancel")
    boolean order(BusinessActionContext context, @BusinessActionContextParameter(paramName = "storageId") Long storageId,
                           @BusinessActionContextParameter(paramName = "count") Integer count);


    /**
     * 确认方法、可以另命名，但要保证与commitMethod一致
     * context可以传递try方法的参数
     * 参数是固定的, 不可以增加或减少,
     * @param context
     * @return
     */
    boolean commitTcc(BusinessActionContext context);


    /**
     * 二阶段取消方法
     * 参数是固定的, 不可以增加或减少
     * @param context
     * @return
     */
    boolean cancel(BusinessActionContext context);



}
