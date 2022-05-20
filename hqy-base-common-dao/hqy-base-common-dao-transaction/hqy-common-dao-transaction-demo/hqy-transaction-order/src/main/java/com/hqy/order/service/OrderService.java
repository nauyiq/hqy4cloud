package com.hqy.order.service;

import com.hqy.base.BaseTkService;
import com.hqy.base.common.bind.MessageResponse;
import com.hqy.order.common.entity.Order;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 10:46
 */
public interface OrderService extends BaseTkService<Order, Long> {

    /**
     * 基于seata的AT模式分布式方案
     * seata的AT模式就是基于2pc进行了优化 在第一阶段prepare阶段直接将本地事务全部提交, 不会hold住资源, 提高了效率 之后第二阶段commit/rollback 判断是否需要回滚事务 <br/>
     * 如果需要则通过数据库表中的undo_log 进行事务补偿.
     * @param storageId 商品id
     * @param count     数目
     * @return MessageResponse
     */
    MessageResponse seataATOrder(Long storageId, Integer count);

    /**
     * 基于seata的TCC模式分布式方案
     * TCC（Try Confirm Cancel）是应用层的两阶段提交，所以对代码的侵入性强，
     * 其核心思想是：针对每个操作，都要实现对应的确认和补偿操作，也就是业务逻辑的每个分支都需要实现 try、confirm、cancel 三个操作，第一阶段由业务代码编排来调用Try接口进行资源预留，
     * 当所有参与者的 Try 接口都成功了，事务协调者提交事务，并调用参与者的 confirm 接口真正提交业务操作，否则调用每个参与者的 cancel 接口回滚事务，并且由于 confirm 或者 cancel
     * 有可能会重试，因此对应的部分需要支持幂等。
     *
     *
     *
     * TCC的注意事项：
     *  （1）允许空回滚
     *  （2）防悬挂控制
     *  （3）幂等控制
     *
     * @param storageId 商品id
     * @param count     数目
     * @return MessageResponse
     */
    MessageResponse seataTccOrder(Long storageId, Integer count);

    /**
     * 基于mq 本地消息表的 分布式事务下单
     * @param storageId 库存id
     * @param count     数目
     * @return
     */
    MessageResponse mqOrderDemo(Long storageId, Integer count);

    /**
     * 基于kafka 实现本地消息表的 分布式事务下单demo
     * @param storageId 库存id
     * @param count     数目
     * @return messageResponse
     */
    MessageResponse kafkaOrder(Long storageId, Integer count);

    /**
     * 基于rocket mq 实现分布式事务下单demo
     * @param storageId
     * @param count
     * @return
     */
    MessageResponse rocketMqOrder(Long storageId, Integer count);
}
