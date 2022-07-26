package com.hqy.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hqy.common.entity.account.Wallet;
import com.hqy.common.entity.order.Order;
import com.hqy.common.entity.storage.Storage;
import com.hqy.common.service.StorageRemoteService;
import com.hqy.common.service.WalletRemoteService;
import com.hqy.order.service.OrderTkService;
import com.hqy.order.service.TccOderService;
import com.hqy.rpc.nacos.client.starter.RPCClient;
import com.hqy.util.AssertUtil;
import com.hqy.util.JsonUtil;
import io.seata.core.context.RootContext;
import io.seata.rm.tcc.api.BusinessActionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/29 16:15
 */
@Slf4j
@Service
public class TccOrderServiceImpl implements TccOderService {

    @Resource
    private OrderTkService orderService;

    /**
     * 用于标记某个订单在当前tcc事务中是否进行过空回滚 防止悬挂。
     */
    private static final Cache<String, Boolean> BLANK_ROLLBACK_CACHE =
            CacheBuilder.newBuilder().initialCapacity(256).expireAfterAccess(1, TimeUnit.HOURS).build();

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public boolean order(Integer count, BigDecimal totalMoney, Wallet account, Storage storage, Order order) {

        log.info("Tcc order, orderId:{}, xid:{}", order.getId(), RootContext.getXID());

        //防悬挂控制
        if (Boolean.TRUE.equals(BLANK_ROLLBACK_CACHE.getIfPresent(order.getId() + ""))) {
            return false;
        }
        //下单
        AssertUtil.isTrue(orderService.insert(order), "下单失败.");

        //减库存
        final String beforeStorage = JsonUtil.toJson(storage);
//        storage.setUsed(storage.getUsed() + count);
//        storage.setResidue(storage.getResidue() - count);
        StorageRemoteService storageRemoteService = RPCClient.getRemoteService(StorageRemoteService.class);
        AssertUtil.isTrue(storageRemoteService.tccModifyStorage(beforeStorage, JsonUtil.toJson(storage)), "@@@ 修改账户余额失败");

        //减账户余额
        final String beforeAccount = JsonUtil.toJson(account);
//        account.setUsed(account.getUsed().add(totalMoney));
//        account.setResidue(account.getResidue().subtract(totalMoney));
        WalletRemoteService accountRemoteService = RPCClient.getRemoteService(WalletRemoteService.class);
        AssertUtil.isTrue(accountRemoteService.tccModifyAccount(beforeAccount ,JsonUtil.toJson(account)), "@@@ 修改账户余额失败");

        return true;
    }

    @Override
    public boolean commitTcc(BusinessActionContext context) {
        JSONObject orderJsonObject = (JSONObject)context.getActionContext("order");
        Order order = orderJsonObject.toJavaObject(Order.class);

        //修改订单状态
        order.setStatus(true);
        boolean modify = orderService.update(order);
        if (!modify) {
            throw new RuntimeException("@@@ 修改订单状态失败");
        }
        return true;
    }

    @Override
    public boolean cancel(BusinessActionContext context) {
        Long orderId = null;
        try {
            JSONObject orderString = (JSONObject)context.getActionContext("order");
            Order order = orderString.toJavaObject(Order.class);
            orderId = order.getId();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        if (orderId == null) {
            return true;
        }

        Order order = orderService.queryById(orderId);
        /*
          1.允许空回滚. 即try超时或者丢包，导致TCC分布式事务二阶段的回滚
          2.悬挂指的是二阶段的 Cancel 比 一阶段的Try 操作先执行，出现该问题的原因是 Try 由于网络拥堵而超时，
          导致事务管理器生成回滚，触发 Cancel 接口，但之后拥堵在网络的 Try 操作又被资源管理器收到了，但是 Cancel 比 Try 先到
         */
        if (order == null) {
            //防悬挂控制 标记当前订单id已经进行cancel  并且是空回滚.
            BLANK_ROLLBACK_CACHE.put(orderId + "", true);
            return true;
        }

        return orderService.deleteByPrimaryKey(order.getId());
    }
}
