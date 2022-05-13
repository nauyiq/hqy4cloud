package com.hqy.account.listener;

import com.hqy.account.service.AccountService;
import com.hqy.mq.rabbitmq.config.RabbitTransactionMessageRecordConfiguration;
import com.hqy.mq.rabbitmq.listener.AbstractRabbitListener;
import com.hqy.mq.rabbitmq.listener.strategy.ListenerStrategy;
import com.hqy.order.common.entity.Account;
import com.hqy.order.common.entity.Order;
import com.hqy.order.common.entity.OrderMessageRecord;
import com.hqy.order.common.service.OrderRemoteService;
import com.hqy.rpc.RPCClient;
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
//@Component
@RabbitListener(queues = RabbitTransactionMessageRecordConfiguration.QUEUE)
public class ReceiverOrderListener extends AbstractRabbitListener<OrderMessageRecord> {

    @Resource
    private AccountService accountService;

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


                    Account account = accountService.queryById(1L);
                    //TODO 减账户余额 需要重新判断条件.

                    //TODO 减账户余额 需要重新判断条件.

                    //减账户余额
                    boolean update = accountService.update(account);

                    int retryTime = 1;
                    if (!update) {
                        while (!update) {
                            if (retryTime > 10) {
                                break;
                            }
                            try {
                                //FIXME 正常来说应该重新判断账户条件...
                                update = accountService.update(account);
                                TimeUnit.MILLISECONDS.sleep(30);
                            } catch (Exception e) {
                                log.error(e.getMessage(), e);
                            }
                            retryTime++;
                        }
                    }

                    if (update)  {
                        //TODO 发消息给order服务 说明扣款成功
                    } else {
                        //TODO 更新余额失败... 发一个回滚消息
                    }
                }
            }

            @Override
            public void compensate(OrderMessageRecord  commonMessageRecord) throws RuntimeException {
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
