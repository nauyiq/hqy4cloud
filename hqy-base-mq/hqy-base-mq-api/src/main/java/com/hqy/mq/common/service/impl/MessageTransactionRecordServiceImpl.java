package com.hqy.mq.common.service.impl;

import com.hqy.base.BaseDao;
import com.hqy.base.impl.BaseTkServiceImpl;
import com.hqy.mq.common.entity.MessageRecord;
import com.hqy.mq.common.mapper.MessageRecordDao;
import com.hqy.mq.common.service.DeliveryMessageService;
import com.hqy.mq.common.service.MessageTransactionRecordService;
import com.hqy.util.AssertUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 抽象的消息
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/7 10:18
 */
@Service
public abstract class MessageTransactionRecordServiceImpl<T> extends BaseTkServiceImpl<MessageRecord<T>, Long> implements MessageTransactionRecordService<T>  {

    public MessageTransactionRecordServiceImpl(DeliveryMessageService deliveryMessageService) {
        this.deliveryMessageService = deliveryMessageService;
    }

    @Resource
    private MessageRecordDao<T> messageRecordDao;

    DeliveryMessageService deliveryMessageService;

    @Override
    public BaseDao<MessageRecord<T>, Long> selectDao() {
        return messageRecordDao;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public boolean preCommit(MessageRecord<T> messageRecord) {
        AssertUtil.notNull(messageRecord, "Mq Transactional preCommit error, messageRecord is null.");
        return insert(messageRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public boolean commit(String messageId, boolean commit) {
        AssertUtil.notEmpty(messageId, "Mq Transactional preCommit error, messageId is empty.");
        //不提交则表示回滚预提交的消息
        if (!commit) {
            return delete(new MessageRecord<>(messageId));
        }
        MessageRecord<T> tMessageRecord = queryOne(new MessageRecord<>(messageId));
        return deliveryMessageService.deliveryMessage(tMessageRecord);
    }
}
