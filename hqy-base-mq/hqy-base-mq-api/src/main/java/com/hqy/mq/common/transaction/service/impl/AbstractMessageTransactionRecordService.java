package com.hqy.mq.common.transaction.service.impl;

import com.hqy.base.BaseDao;
import com.hqy.base.impl.BaseTkServiceImpl;
import com.hqy.mq.common.MessageQueueType;
import com.hqy.mq.common.transaction.entity.CommonMessageRecord;
import com.hqy.mq.common.transaction.service.MessageTransactionService;
import com.hqy.mq.common.transaction.stategy.DeliveryMessageContext;
import com.hqy.mq.common.transaction.stategy.DeliveryMessageStrategy;
import com.hqy.util.AssertUtil;
import com.hqy.util.ReflectClassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/12 16:09
 */
public abstract class AbstractMessageTransactionRecordService<T extends CommonMessageRecord>
        extends BaseTkServiceImpl<T, Long> implements MessageTransactionService<T> {

    private static final Logger log = LoggerFactory.getLogger(AbstractMessageTransactionRecordService.class);

    /**
     * 由子类注入的orm dao 对象
     * @return BaseDao<T, Long>
     */
    public abstract BaseDao<T, Long> registryDao();

    /**
     * 由子类决定使用何种的消息队列中间件
     * @return MessageQueueType
     */
    public abstract MessageQueueType registryQueueType();

    @Override
    public BaseDao<T, Long> selectDao() {
        return registryDao();
    }

    @Override
    public boolean preCommit(T payload) {
        AssertUtil.notNull(payload, "Insert message record failure, message payload is null.");
        return insert(payload);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean commit(String messageId, boolean commit) {
        //获取到泛型的类型
        Class<? extends CommonMessageRecord> targetGenericClass = ReflectClassUtil.getTargetGenericClass(getClass(), 0);
        T t;
        try {
            t = (T) targetGenericClass.newInstance();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        t.setMessageId(messageId);

        //不提交则直接删除本地消息表的数据.
        if (!commit) {
            return delete(t);
        }
        //根据消息id 从本地消息表中获取唯一的数据
        t = queryOne(t);
        if (t == null) {
            return false;
        }
        //投递消息到对应的中间件.
        DeliveryMessageStrategy deliveryMessageStrategy = DeliveryMessageContext.deliveryMessageStrategy(registryQueueType());
        return deliveryMessageStrategy.deliveryMessage(t);
    }
}
