package com.hqy.account.controller;

import com.hqy.account.service.AccountService;
import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.bind.MessageResponse;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.order.common.entity.Account;
import com.hqy.order.common.entity.Storage;
import com.hqy.order.common.service.AccountRemoteService;
import com.hqy.order.common.service.OrderRemoteService;
import com.hqy.order.common.service.StorageRemoteService;
import com.hqy.rpc.RPCClient;
import com.hqy.util.JsonUtil;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 15:38
 */
@Slf4j
@RestController
public class TransactionController {

    @Resource
    private AccountService accountService;

    @Resource
    private AccountRemoteService accountRemoteService;

    @PostMapping("/transaction/buy")
    @GlobalTransactional(timeoutMills = 3000000, name = "buy-service")
    public MessageResponse buy(Long storageId, Integer count) {
        log.info("开始全局事务，XID = " + RootContext.getXID());
        Account account = accountService.queryById(1L);
        if (account == null) {
            return new MessageResponse(false, CommonResultCode.SYSTEM_BUSY.message, CommonResultCode.SYSTEM_BUSY.code);
        }
        if (storageId == null) {
            storageId = 1L;
        }
        if (count == null || count <= 0) {
            count = 1;
        }
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
        OrderRemoteService orderRemoteService = RPCClient.getRemoteService(OrderRemoteService.class);
        Long orderNum = orderRemoteService.order(storageId, count, totalMoney.toString());
        if(orderNum == null) {
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
            return new MessageResponse(false, CommonResultCode.SYSTEM_BUSY.message, CommonResultCode.SYSTEM_BUSY.code);
        }

        return new DataResponse(true, CommonResultCode.SUCCESS.message, CommonResultCode.SUCCESS.code, orderNum);
    }


}
