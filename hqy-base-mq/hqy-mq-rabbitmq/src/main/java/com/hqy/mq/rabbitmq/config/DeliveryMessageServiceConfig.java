package com.hqy.mq.rabbitmq.config;

import com.hqy.mq.common.service.DeliveryMessageService;
import com.hqy.mq.common.service.MessageTransactionRecordService;
import com.hqy.mq.rabbitmq.RabbitmqProcessor;
import com.hqy.util.spring.SpringContextHolder;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/10 15:49
 */
@Configuration
public class DeliveryMessageServiceConfig {


    @Bean
    @SuppressWarnings("unchecked")
    public DeliveryMessageService rabbitDeliveryService() {
        return messageRecord -> {
            try {
                CorrelationData correlationData = new CorrelationData(messageRecord.getMessageId());
                correlationData.getFuture().addCallback(ackCallBack -> {
                    if (ackCallBack == null || !ackCallBack.isAck()) {
                        //重发消息
                        RabbitmqProcessor.getInstance().sendMessage(RabbitTransactionMessageRecordConfiguration.EXCHANGE,  "", messageRecord, correlationData);
                    } else {
                        MessageTransactionRecordService service = SpringContextHolder.getBean(MessageTransactionRecordService.class);
                        messageRecord.setStatus(true);
                        service.updateMessage(messageRecord);
                    }
                }, failCallback -> RabbitmqProcessor.getInstance().sendMessage(RabbitTransactionMessageRecordConfiguration.EXCHANGE, "", messageRecord, correlationData));

                RabbitmqProcessor.getInstance().sendMessage(RabbitTransactionMessageRecordConfiguration.EXCHANGE, "", messageRecord, correlationData);
            } catch (Exception e) {
                return false;
            }
            return true;
        };
    }


}
