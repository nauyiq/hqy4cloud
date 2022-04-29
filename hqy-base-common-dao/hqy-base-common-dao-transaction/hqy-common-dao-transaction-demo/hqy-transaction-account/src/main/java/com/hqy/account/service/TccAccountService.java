package com.hqy.account.service;

import com.hqy.order.common.entity.Account;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;

/**
 * 基于TCC模式的账号service
 * @LocalTCC 一定需要注解在接口上 表示当前接口是TCC接口
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/29 14:26
 */
@LocalTCC
public interface TccAccountService {

    /**
     * 定义两阶段提交
     * name = 该tcc的bean名称,全局唯一
     * commitMethod = commit 为二阶段确认方法
     * rollbackMethod = rollback 为二阶段取消方法
     * BusinessActionContextParameter注解 传递参数到二阶段中
     * @param beforeAccount
     * @param afterAccount
     * @return
     */
    @TwoPhaseBusinessAction(name = "modifyAccount", commitMethod = "commitTcc", rollbackMethod = "cancel")
    boolean modifyAccount( @BusinessActionContextParameter(paramName = "beforeAccount") Account beforeAccount,
                           @BusinessActionContextParameter(paramName = "afterAccount") Account afterAccount);


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
