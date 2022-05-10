package com.hqy.order.service.impl;

import com.hqy.base.BaseDao;
import com.hqy.base.impl.BaseTkServiceImpl;
import com.hqy.mq.common.entity.CommonMessageRecord;
import com.hqy.mq.common.service.DeliveryMessageService;
import com.hqy.mq.common.service.MessageTransactionRecordService;
import com.hqy.order.common.entity.OrderMessageRecord;
import com.hqy.order.dao.OrderMessageRecordDao;
import com.hqy.util.AssertUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 抽象的消息
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/7 10:18
 */
@Service
public class OrderMessageTransactionRecordServiceImpl extends BaseTkServiceImpl<OrderMessageRecord, Long> implements MessageTransactionRecordService  {

    @Resource
    private OrderMessageRecordDao messageRecordDao;

    @Resource
    public DeliveryMessageService deliveryMessageService;


    @Override
    public BaseDao<OrderMessageRecord, Long> selectDao() {
        return messageRecordDao;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public boolean preCommit(CommonMessageRecord commonMessageRecord) {
        AssertUtil.notNull(commonMessageRecord, "Mq Transactional preCommit error, messageRecord is null.");
        return insert((OrderMessageRecord) commonMessageRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public boolean commit(String messageId, boolean commit) {
        AssertUtil.notEmpty(messageId, "Mq Transactional preCommit error, messageId is empty.");
        //不提交则表示回滚预提交的消息
        if (!commit) {
            return delete(new OrderMessageRecord(messageId));
        }
        OrderMessageRecord tCommonMessageRecord = queryOne(new OrderMessageRecord(messageId));
        return deliveryMessageService.deliveryMessage(tCommonMessageRecord);
    }

    @Override
    public List<CommonMessageRecord> queryAllMessage() {
        List<OrderMessageRecord> orderMessageRecords = queryAll();
        if (CollectionUtils.isNotEmpty(orderMessageRecords)) {
            return orderMessageRecords.stream().map(e -> (CommonMessageRecord)e).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public boolean updateMessage(CommonMessageRecord messageRecord) {
        return update((OrderMessageRecord)messageRecord);
    }
}
