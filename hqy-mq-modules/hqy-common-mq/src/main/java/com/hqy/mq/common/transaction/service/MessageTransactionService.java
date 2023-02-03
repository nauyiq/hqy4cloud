package com.hqy.mq.common.transaction.service;

import com.hqy.base.BaseTkService;
import com.hqy.mq.common.transaction.entity.CommonMessageRecord;

import java.util.List;

/**
 * 分布式事务 （无事务）mq + 本地消息表 消息处理service.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/7 9:32
 */
public interface MessageTransactionService<T extends CommonMessageRecord> extends BaseTkService<T, Long> {

    /**
     * 预提交.
     * @param  payload 消息表实体.
     * @return         是否入库成功.
     */
    boolean preCommit(T payload);

    /**
     * 提交本地消息，即把本地消息投递到消息中间件.
     * @param messageId 消息id
     * @param commit    是否提交, 不提交则回滚预提交的消息.
     * @return
     */
    boolean commit(String messageId, boolean commit);




}
