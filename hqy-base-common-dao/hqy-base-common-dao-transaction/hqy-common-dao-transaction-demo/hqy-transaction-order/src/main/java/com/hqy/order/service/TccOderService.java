package com.hqy.order.service;

import com.hqy.order.common.entity.Account;
import com.hqy.order.common.entity.Order;
import com.hqy.order.common.entity.Storage;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;

import java.math.BigDecimal;

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
    boolean order(@BusinessActionContextParameter(paramName = "storageId" ) Long storageId,
                  @BusinessActionContextParameter(paramName = "count" )Integer count,
                  @BusinessActionContextParameter(paramName = "totalMoney" )BigDecimal totalMoney,
                  @BusinessActionContextParameter(paramName = "account" )Account account,
                  @BusinessActionContextParameter(paramName = "storage" )Storage storage,
                  @BusinessActionContextParameter(paramName = "accountJson" )String accountJson,
                  @BusinessActionContextParameter(paramName = "storageJson" )String storageJson,
                  @BusinessActionContextParameter(paramName = "order" ) Order order);


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
