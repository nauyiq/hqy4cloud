package com.hqy.order.service.impl;

import com.hqy.order.common.entity.Account;
import com.hqy.order.common.entity.Order;
import com.hqy.order.common.entity.Storage;
import com.hqy.order.common.service.AccountRemoteService;
import com.hqy.order.common.service.OrderRemoteService;
import com.hqy.order.common.service.StorageRemoteService;
import com.hqy.order.service.OrderService;
import com.hqy.rpc.RPCClient;
import com.hqy.rpc.api.AbstractRPCService;
import com.hqy.util.JsonUtil;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 11:14
 */
@Service
public class OrderRemoteServiceImpl extends AbstractRPCService implements OrderRemoteService {

    @Resource
    private OrderService orderService;

    @Override
    @GlobalTransactional(timeoutMills = 3000000, name = "test-buy", rollbackFor = Exception.class)
    public Long order(Long productId, int count, String money, String storageJson, String accountJson) {
        Order order = new Order();
        order.setProductId(productId);
        order.setCount(count);
        order.setMoney(new BigDecimal(money));
        order.setStatus(false);
        order.setCreated(new Date());
        order.setUpdated(new Date());
        order.setAccountId(1L);
        Long orderNum = orderService.insertReturnPk(order);

        Storage storage = JsonUtil.toBean(storageJson, Storage.class);

        //减库存
        storage.setUsed(storage.getUsed() + count);
        storage.setResidue(storage.getResidue() - count);

        StorageRemoteService storageRemoteService = RPCClient.getRemoteService(StorageRemoteService.class);
        boolean modify =  storageRemoteService.modifyStorage(JsonUtil.toJson(storage));
        if (!modify) {
            throw new RuntimeException("@@@ 修改库存数目失败");
        }
        Account account = JsonUtil.toBean(accountJson, Account.class);
        //减账户余额
        BigDecimal totalMoney = new BigDecimal(money);
        account.setUsed(account.getUsed().add(totalMoney));
        account.setResidue(account.getResidue().subtract(totalMoney));
        AccountRemoteService accountRemoteService = RPCClient.getRemoteService(AccountRemoteService.class);
        modify = accountRemoteService.modifyAccount(JsonUtil.toJson(account));
        if (!modify) {
            throw new RuntimeException("@@@ 修改账户余额失败");
        }

        //修改订单状态
        order.setId(orderNum);
        order.setStatus(true);
        orderService.update(order);

        return orderNum;


    }

    @Override
    @GlobalTransactional(timeoutMills = 3000000, name = "test-buy", rollbackFor = Exception.class)
    public void updateOrderStateSuccess(Long orderNum) {
        Order order = orderService.queryById(orderNum);
        if (order == null) {
            return;
        }
        order.setStatus(true);
        orderService.update(order);
    }
}
