package com.hqy.order.service.impl;

import com.alibaba.fastjson.JSONObject;
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
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

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
    public boolean order( Long storageId, Integer count, BigDecimal totalMoney,Account account, Storage storage,  String accountJson, String storageJson,Order order) {


        //减库存
        storage.setUsed(storage.getUsed() + count);
        storage.setResidue(storage.getResidue() - count);

        StorageRemoteService storageRemoteService = RPCClient.getRemoteService(StorageRemoteService.class);
        boolean modify =  storageRemoteService.tccModifyStorage(storageJson, JsonUtil.toJson(storage));
        if (!modify) {
            throw new RuntimeException("@@@ 修改账户余额失败");
        }


        //减账户余额
        account.setUsed(account.getUsed().add(totalMoney));
        account.setResidue(account.getResidue().subtract(totalMoney));

        AccountRemoteService accountRemoteService = RPCClient.getRemoteService(AccountRemoteService.class);
        modify = accountRemoteService.tccModifyAccount(accountJson ,JsonUtil.toJson(account));
        if (!modify) {
            throw new RuntimeException("@@@ 修改账户余额失败");
        }




        return true;
    }

    @Override
    public boolean commitTcc(BusinessActionContext context) {

        JSONObject orderString = (JSONObject)context.getActionContext("order");
        Order order = orderString.toJavaObject(Order.class);
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
        JSONObject orderString = (JSONObject)context.getActionContext("order");
        Order order = orderString.toJavaObject(Order.class);
        if (order != null) {
            return orderService.deleteById(order.getId());
        }
        return true;
    }
}
