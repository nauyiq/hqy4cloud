package com.hqy.order.service.impl;

import com.hqy.common.entity.account.Wallet;
import com.hqy.common.entity.order.Order;
import com.hqy.common.entity.storage.Storage;
import com.hqy.common.service.OrderRemoteService;
import com.hqy.common.service.StorageRemoteService;
import com.hqy.common.service.WalletRemoteService;
import com.hqy.order.service.OrderTkService;
import com.hqy.rpc.RPCClient;
import com.hqy.rpc.api.AbstractRPCService;
import com.hqy.util.JsonUtil;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 11:14
 */
@Service
public class OrderRemoteServiceImpl extends AbstractRPCService implements OrderRemoteService {

    @Resource
    private OrderTkService orderService;

    @Override
    public String queryOrderById(Long orderId) {
        Order order = orderService.queryById(orderId);
        if (Objects.isNull(order)) {
            return "";
        }
        return JsonUtil.toJson(order);
    }

    @Override
    @GlobalTransactional(timeoutMills = 3000000, name = "test-buy", rollbackFor = Exception.class)
    public Long order(Long productId, int count, String money, String storageJson, String accountJson) {
        String xid = RootContext.getXID();
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
//        storage.setUsed(storage.getUsed() + count);
//        storage.setResidue(storage.getResidue() - count);

        StorageRemoteService storageRemoteService = RPCClient.getRemoteService(StorageRemoteService.class);
        boolean modify =  storageRemoteService.modifyStorage(JsonUtil.toJson(storage));
        if (!modify) {
            throw new RuntimeException("@@@ 修改库存数目失败");
        }
        Wallet account = JsonUtil.toBean(accountJson, Wallet.class);
        //减账户余额
        BigDecimal totalMoney = new BigDecimal(money);
//        account.setUsed(account.getUsed().add(totalMoney));
//        account.setResidue(account.getResidue().subtract(totalMoney));
        WalletRemoteService accountRemoteService = RPCClient.getRemoteService(WalletRemoteService.class);
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
