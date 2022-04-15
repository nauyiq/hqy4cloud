package com.hqy.order.service.impl;

import com.hqy.base.BaseDao;
import com.hqy.base.common.bind.MessageResponse;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.base.common.swticher.CommonSwitcher;
import com.hqy.base.impl.BaseTkServiceImpl;
import com.hqy.order.common.entity.Account;
import com.hqy.order.common.entity.Order;
import com.hqy.order.common.entity.Storage;
import com.hqy.order.common.service.AccountRemoteService;
import com.hqy.order.common.service.StorageRemoteService;
import com.hqy.order.dao.OrderDao;
import com.hqy.order.service.OrderService;
import com.hqy.rpc.RPCClient;
import com.hqy.util.JsonUtil;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 10:46
 */
@Service
public class OrderServiceImpl extends BaseTkServiceImpl<Order, Long> implements OrderService {

    @Resource
    private OrderDao orderDao;

    @Override
    public BaseDao<Order, Long> selectDao() {
        return orderDao;
    }

    @Override
    @GlobalTransactional( rollbackFor = Exception.class)
    public MessageResponse order(Long storageId, Integer count) {

        CommonSwitcher.JUST_4_TEST_DEBUG.setStatus(false);

        AccountRemoteService accountRemoteService = RPCClient.getRemoteService(AccountRemoteService.class);
        String accountJson = accountRemoteService.queryById(1L);
        if (StringUtils.isBlank(accountJson)) {
            return new MessageResponse(false, CommonResultCode.USER_NOT_FOUND.message, CommonResultCode.USER_NOT_FOUND.code);
        }
        Account account = JsonUtil.toBean(accountJson, Account.class);
        //获取库存
        StorageRemoteService storageRemoteService = RPCClient.getRemoteService(StorageRemoteService.class);
        String storageJson = storageRemoteService.getStorage(storageId);
        if (StringUtils.isBlank(storageJson)) {
            return new MessageResponse(false, CommonResultCode.SYSTEM_BUSY.message, CommonResultCode.SYSTEM_BUSY.code);
        }
        Storage storage = JsonUtil.toBean(storageJson, Storage.class);
        //判断是否能下单
        BigDecimal residue = account.getResidue();
        BigDecimal price = storage.getPrice();
        BigDecimal totalMoney = price.multiply(new BigDecimal(count));
        if (residue.compareTo(totalMoney) < 0 || storage.getResidue() < count) {
            return new MessageResponse(false, CommonResultCode.SYSTEM_BUSY.message, CommonResultCode.SYSTEM_BUSY.code);
        }

        //下单
        Order order = new Order(1L, storageId, count, totalMoney, false, new Date());
        Long orderNum = insertReturnPk(order);
        if (orderNum == null) {
            return new MessageResponse(false, CommonResultCode.SYSTEM_BUSY.message, CommonResultCode.SYSTEM_BUSY.code);
        }

        //减库存
        storage.setUsed(storage.getUsed() + count);
        storage.setResidue(storage.getResidue() - count);
        boolean modify =  storageRemoteService.modifyStorage(JsonUtil.toJson(storage));
        if (!modify) {
            return new MessageResponse(false, CommonResultCode.SYSTEM_BUSY.message, CommonResultCode.SYSTEM_BUSY.code);
        }

        //减账户余额
        account.setUsed(account.getUsed().add(totalMoney));
        account.setResidue(account.getResidue().subtract(totalMoney));
        modify = accountRemoteService.modifyAccount(JsonUtil.toJson(account));
        if (!modify) {
            throw new RuntimeException("@@@ 修改账户余额失败");
        }

        //修改订单状态
        order.setId(orderNum);
        order.setStatus(true);
        modify = update(order);

        if (!modify) {
            throw new RuntimeException("@@@ 修改订单状态失败");
        }


        return new MessageResponse(true, CommonResultCode.SUCCESS.message, CommonResultCode.SUCCESS.code);
    }
}
