package com.hqy.mq.common.mapper;

import com.hqy.base.BaseDao;
import com.hqy.mq.common.entity.MessageRecord;
import org.springframework.stereotype.Repository;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/7 10:21
 */
@Repository
public interface MessageRecordDao<T> extends BaseDao<MessageRecord<T>, Long> {

}
