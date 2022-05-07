package com.hqy.mq.rabbitmq.task;

import com.hqy.base.common.swticher.CommonSwitcher;
import com.hqy.mq.common.entity.MessageRecord;
import com.hqy.mq.common.service.MessageTransactionRecordService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 扫描本地消息表的定时任务
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/7 15:45
 */
@Component
@EnableScheduling
@SuppressWarnings({"rawtypes", "unchecked"})
public class RabbitMessageRecordConfirmTask {

    private static final Logger log = LoggerFactory.getLogger(RabbitMessageRecordConfirmTask.class);

    @Resource
    private MessageTransactionRecordService messageTransactionRecordService;


    /**
     * 每隔一分钟轮训查询本地消息表
     */
    @Scheduled(cron = "0 0/2 * * * ?")
    public void confirm() {
        List<MessageRecord> messageRecords = messageTransactionRecordService.queryAll();
        if (CollectionUtils.isEmpty(messageRecords)) {
            return;
        }

        if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
            log.info("[RabbitMessageRecordConfirmTask] scan message table list begin. count:{}", messageRecords.size());
        }

        for (MessageRecord messageRecord : messageRecords) {
            if (!messageRecord.getStatus()) {
                //解决存放在本地事务表中 但是状态是false的消息 说明当前消息需要发消息
                messageTransactionRecordService.commit(messageRecord.getMessageId(), true);
            }
        }
    }


}
