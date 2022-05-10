package com.hqy.storage.listener;

import com.hqy.fundation.cache.redis.LettuceRedis;
import com.hqy.mq.common.entity.CommonMessageRecord;
import com.hqy.mq.rabbitmq.config.RabbitTransactionMessageRecordConfiguration;
import com.hqy.mq.rabbitmq.listener.AbstractRabbitListener;
import com.hqy.mq.rabbitmq.listener.strategy.ListenerStrategy;
import com.hqy.order.common.entity.Order;
import com.hqy.order.common.entity.OrderMessageRecord;
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
import java.util.concurrent.TimeUnit;

/**
 * 监听到订单消息
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/7 16:58
 */
@Slf4j
@Component
@RabbitListener(queues = RabbitTransactionMessageRecordConfiguration.QUEUE)
public class ReceiverOrderListener extends AbstractRabbitListener<OrderMessageRecord> {


    @Resource
    private StorageService storageService;

    @Override
    public ListenerStrategy<OrderMessageRecord> strategy() {

        return new ListenerStrategy<OrderMessageRecord>() {

            @Override
            public void action(OrderMessageRecord commonMessageRecord) throws RuntimeException {
                checkParameter(commonMessageRecord);
                String messageId = commonMessageRecord.getMessageId();
                Long orderId = commonMessageRecord.getBusinessId();
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
                    } else {
                        //TODO 发消息给order服务 说明当前订单已经扣完库存了
                    }
                }
            }



            @Override
            public void compensate(OrderMessageRecord commonMessageRecord) throws RuntimeException {
                //TODO 更新库存失败... 发一个回滚消息
            }
        };
    }


    private void checkParameter(OrderMessageRecord commonMessageRecord) {
        if (StringUtils.isEmpty( commonMessageRecord.getMessageId()) || Objects.isNull(commonMessageRecord.getBusinessId())) {
            log.error("[ReceiverOrderListener] invalid parameter, payload:{}", JsonUtil.toJson(commonMessageRecord));
            throw new IllegalArgumentException("[ReceiverOrderListener] invalid parameter.");
        }
    }


}
