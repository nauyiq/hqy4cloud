package com.hqy.storage.listener;

import com.hqy.fundation.cache.redis.LettuceRedis;
import com.hqy.mq.common.entity.MessageRecord;
import com.hqy.mq.rabbitmq.config.RabbitTransactionMessageRecordConfiguration;
import com.hqy.mq.rabbitmq.listener.AbstractRabbitListener;
import com.hqy.mq.rabbitmq.listener.strategy.ListenerStrategy;
import com.hqy.order.common.entity.Order;
import com.hqy.order.common.entity.Storage;
import com.hqy.order.common.service.OrderRemoteService;
import com.hqy.rpc.RPCClient;
import com.hqy.storage.service.StorageService;
import com.hqy.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 监听到订单消息
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/7 16:58
 */
@Slf4j
@Component
@RabbitListener(queues = RabbitTransactionMessageRecordConfiguration.QUEUE)
public class ReceiverOrderListener extends AbstractRabbitListener<MessageRecord<Long>> {


    @Resource
    private StorageService storageService;

    @Override
    public ListenerStrategy<MessageRecord<Long>> strategy() {

        return new ListenerStrategy<MessageRecord<Long>>() {

            @Override
            public void action(MessageRecord<Long> messageRecord) throws RuntimeException {
                checkParameter(messageRecord);
                String messageId = messageRecord.getMessageId();
                Long orderId = messageRecord.getBusinessId();
                //根据订单id 获取订单数据
                OrderRemoteService orderRemoteService = RPCClient.getRemoteService(OrderRemoteService.class);
                String orderJson = orderRemoteService.queryOrderById(orderId);
                if (StringUtils.isBlank(orderJson)) {
                    //TODO 查询不到订单...发一个回滚消息...
                } else {
                    Order order = JsonUtil.toBean(orderJson, Order.class);
                    Long productId = order.getProductId();
                    Integer count = order.getCount();
                    //获取当时下单查询到的库存
                    Storage storage = LettuceRedis.getInstance().get(messageId);
                    if (Objects.isNull(storage)) {
                        storage = storageService.queryById(productId);
                    }

                    if (Objects.isNull(storage)) {
                        //TODO 查询不到库存...发一个回滚消息...
                        return;
                    }

                    //乐观锁 CAS 更新库存
                    boolean casUpdate = storageService.casUpdate(productId, storage.getUsed() + count, storage.getResidue() - count, storage.getResidue());









                }


            }



            @Override
            public void compensate(MessageRecord<Long> messageRecord) throws RuntimeException {

            }
        };
    }


    private void checkParameter(MessageRecord<Long> messageRecord) {
        if (StringUtils.isEmpty( messageRecord.getMessageId()) || Objects.isNull(messageRecord.getBusinessId())) {
            log.error("[ReceiverOrderListener] invalid parameter, payload:{}", JsonUtil.toJson(messageRecord));
            throw new IllegalArgumentException("[ReceiverOrderListener] invalid parameter.");
        }
    }


}
