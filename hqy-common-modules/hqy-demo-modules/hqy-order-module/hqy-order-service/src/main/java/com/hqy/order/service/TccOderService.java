package com.hqy.order.service;

import com.hqy.common.entity.account.Wallet;
import com.hqy.common.entity.order.Order;
import com.hqy.common.entity.storage.Storage;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;

import java.math.BigDecimal;

/**
 * Seata TCC 模式时 一定是定义在接口上.
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
     * @param count      下单数目
     * @param totalMoney 总额
     * @param Wallet    Account
     * @param storage    Storage
     * @param order      Order
     * @return           是否下单成功
     */
    @TwoPhaseBusinessAction(name = "order", commitMethod = "commitTcc", rollbackMethod = "cancel")
    boolean order(@BusinessActionContextParameter(paramName = "count" )Integer count,
                  @BusinessActionContextParameter(paramName = "totalMoney" )BigDecimal totalMoney,
                  @BusinessActionContextParameter(paramName = "account" )Wallet wallet,
                  @BusinessActionContextParameter(paramName = "storage" ) Storage storage,
                  @BusinessActionContextParameter(paramName = "order" ) Order order);


    /**
     * 确认方法、可以另命名，但要保证与commitMethod一致
     * context可以传递try方法的参数
     * 参数是固定的, 不可以增加或减少,
     * 返回true表示成功 返回false seata会默认不断重试 直到成功为止
     * @param context
     * @return
     */
    boolean commitTcc(BusinessActionContext context);


    /**
     * 二阶段取消方法
     * 参数是固定的, 不可以增加或减少
     * 返回true表示成功 返回false seata会默认不断重试 直到成功为止
     * @param context
     * @return
     */
    boolean cancel(BusinessActionContext context);



}
