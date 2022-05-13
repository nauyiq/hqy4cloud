package com.hqy.storage.listener;

import com.hqy.fundation.cache.redis.LettuceRedis;
import com.hqy.mq.kafka.config.KafkaTransactionalInitialConfiguration;
import com.hqy.order.common.entity.Order;
import com.hqy.order.common.entity.OrderMessageRecord;
import com.hqy.order.common.entity.Storage;
import com.hqy.order.common.service.OrderRemoteService;
import com.hqy.rpc.RPCClient;
import com.hqy.storage.service.StorageService;
import com.hqy.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConsumerAwareListenerErrorHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * kafka + 本地消息表 监听到下单消息 进行库存变更.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/13 9:35
 */
@Slf4j
@Component
public class KafkaOrderMessageListener {

    @Resource
    private StorageService storageService;

    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Bean
    public ConsumerAwareListenerErrorHandler orderConsumerAwareListenerErrorHandler() {
        return (message, exception, consumer) -> {
            //TODO 消费异常 发起回滚消息 进行业务回滚...
            log.error("消费异常："+message.getPayload());
            return null;
        };

    }


    @KafkaListener(id = "storageConsumer", groupId = "storage-group",
            topics = KafkaTransactionalInitialConfiguration.TRANSACTIONAL_TOPIC, concurrency = "${listen.concurrency:1}")
    public void listenerOrderMessage(
                                     ConsumerRecord<?, ?> consumer) {

        String messageJson = (String)consumer.value();
        log.info("storageConsumer2, data:{}", messageJson);

        OrderMessageRecord orderMessageRecord = JsonUtil.toBean(messageJson, OrderMessageRecord.class);
        String messageId = orderMessageRecord.getMessageId();
        Long orderId = orderMessageRecord.getBusinessId();

        //根据订单id 获取订单数据
        OrderRemoteService orderRemoteService = RPCClient.getRemoteService(OrderRemoteService.class);
        String orderJson = orderRemoteService.queryOrderById(orderId);

        if (StringUtils.isBlank(orderJson)) {
            throw new RuntimeException("订单数据为空.");
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
                throw new RuntimeException("获取不到库存.");
            }

            //乐观锁 CAS 更新库存
            boolean casUpdate = storageService.casUpdate(productId, storage.getUsed() + count, storage.getResidue() - count, storage.getResidue());

            int retryTime = 1;
            if (!casUpdate) {
                while (!casUpdate) {
                    if (retryTime > 10) {
                        break;
                    }
                    try {
                        //如果更新失败 有可能是发生了并发问题 即库存数据变更了 重新读取库存
                        Storage queryStorage = storageService.queryById(productId);
                        //FIXME 正常来说应该重新判断下单条件...
                        casUpdate = storageService.casUpdate
                                (productId, queryStorage.getUsed() + count, queryStorage.getResidue() - count, queryStorage.getResidue());
                        TimeUnit.MILLISECONDS.sleep(30);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                    retryTime++;
                }
            }

            if (!casUpdate)  {
                //TODO 更新库存失败... 发一个回滚消息
                throw new RuntimeException("更新库存失败.");
            } else {
                //TODO 发消息给order服务 说明当前订单已经扣完库存了
                kafkaTemplate.send(KafkaTransactionalInitialConfiguration.TRANSACTIONAL_STORAGE_TOPIC, messageJson);
            }

        }




    }


}
