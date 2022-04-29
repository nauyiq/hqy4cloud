package com.hqy.order.service.impl;

import com.hqy.base.common.bind.MessageResponse;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.order.common.entity.Account;
import com.hqy.order.common.entity.Order;
import com.hqy.order.common.entity.Storage;
import com.hqy.order.common.service.AccountRemoteService;
import com.hqy.order.common.service.StorageRemoteService;
import com.hqy.order.service.OrderService;
import com.hqy.order.service.TccOderService;
import com.hqy.rpc.RPCClient;
import com.hqy.util.JsonUtil;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/29 16:15
 */
@Service
public class TccOrderServiceImpl implements TccOderService {

    @Resource
    private OrderService orderService;

    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public boolean order(BusinessActionContext context, Long storageId, Integer count) {
        AccountRemoteService accountRemoteService = RPCClient.getRemoteService(AccountRemoteService.class);
        String accountJson = accountRemoteService.getAccountById(1L);
        if (StringUtils.isBlank(accountJson)) {
            return false;
        }
        //获取库存
        StorageRemoteService storageRemoteService = RPCClient.getRemoteService(StorageRemoteService.class);
        String storageJson = storageRemoteService.getStorage(storageId);
        if (StringUtils.isBlank(storageJson)) {
            return false;
        }

        Account account = JsonUtil.toBean(accountJson, Account.class);

        Storage storage = JsonUtil.toBean(storageJson, Storage.class);



        //判断是否能下单
        BigDecimal residue = account.getResidue();
        BigDecimal price = storage.getPrice();
        BigDecimal totalMoney = price.multiply(new BigDecimal(count));
        if (residue.compareTo(totalMoney) < 0 || storage.getResidue() < count) {
            return false;
        }

        //下单
        Order order = new Order(1L, storageId, count, totalMoney, false, new Date());
        Long orderNum = orderService.insertReturnPk(order);
        if (orderNum == null) {
            return false;
        }
        order.setId(orderNum);

        //减库存
        storage.setUsed(storage.getUsed() + count);
        storage.setResidue(storage.getResidue() - count);
        boolean modify =  storageRemoteService.tccModifyStorage(storageJson, JsonUtil.toJson(storage));
        if (!modify) {
            return false;
        }

        //减账户余额
        account.setUsed(account.getUsed().add(totalMoney));
        account.setResidue(account.getResidue().subtract(totalMoney));
        modify = accountRemoteService.tccModifyAccount(accountJson ,JsonUtil.toJson(account));
        if (!modify) {
            throw new RuntimeException("@@@ 修改账户余额失败");
        }

        Map<String, Object> actionContext = context.getActionContext();
        actionContext.put("order",  order);
        context.setActionContext(actionContext);


        return true;
    }

    @Override
    public boolean commitTcc(BusinessActionContext context) {

        Order order = (Order)context.getActionContext("order");
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
        Order order = (Order)context.getActionContext("order");
        if (order != null) {
            return orderService.deleteById(order.getId());
        }
        return true;
    }
}
