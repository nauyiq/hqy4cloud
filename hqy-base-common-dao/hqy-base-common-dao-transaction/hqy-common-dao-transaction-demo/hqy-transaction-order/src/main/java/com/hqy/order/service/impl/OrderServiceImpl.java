package com.hqy.order.service.impl;

import com.hqy.base.BaseDao;
import com.hqy.base.common.base.lang.BaseMathConstants;
import com.hqy.base.common.bind.MessageResponse;
import com.hqy.base.common.exception.MessageMqException;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.base.common.swticher.CommonSwitcher;
import com.hqy.base.impl.BaseTkServiceImpl;
import com.hqy.fundation.cache.redis.LettuceRedis;
import com.hqy.mq.common.service.MessageTransactionRecordService;
import com.hqy.order.common.entity.Account;
import com.hqy.order.common.entity.Order;
import com.hqy.order.common.entity.OrderMessageRecord;
import com.hqy.order.common.entity.Storage;
import com.hqy.order.common.service.AccountRemoteService;
import com.hqy.order.common.service.StorageRemoteService;
import com.hqy.order.dao.OrderDao;
import com.hqy.order.service.OrderService;
import com.hqy.order.service.TccOderService;
import com.hqy.rpc.RPCClient;
import com.hqy.util.JsonUtil;
import com.hqy.util.identity.ProjectSnowflakeIdWorker;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 10:46
 */
@Slf4j
@Service
public class OrderServiceImpl extends BaseTkServiceImpl<Order, Long> implements OrderService {

    @Resource
    private OrderDao orderDao;

    @Resource
    private TccOderService tccOderService;

    @Resource
    private MessageTransactionRecordService messageTransactionRecordService;

    @Override
    public BaseDao<Order, Long> selectDao() {
        return orderDao;
    }





    @Override
    @GlobalTransactional(timeoutMills = 3000000, name = "test-buy", rollbackFor = Exception.class)
    public MessageResponse order(Long storageId, Integer count) {

        CommonSwitcher.JUST_4_TEST_DEBUG.setStatus(false);

        String xid = RootContext.getXID();
        System.out.println(xid);


        AccountRemoteService accountRemoteService = RPCClient.getRemoteService(AccountRemoteService.class);
        String accountJson = accountRemoteService.getAccountById(1L);
        if (StringUtils.isBlank(accountJson)) {
            return new MessageResponse(false, CommonResultCode.USER_NOT_FOUND.message, CommonResultCode.USER_NOT_FOUND.code);
        }

        //获取库存
        StorageRemoteService storageRemoteService = RPCClient.getRemoteService(StorageRemoteService.class);
        String storageJson = storageRemoteService.getStorage(storageId);
        if (StringUtils.isBlank(storageJson)) {
            return new MessageResponse(false, CommonResultCode.SYSTEM_BUSY.message, CommonResultCode.SYSTEM_BUSY.code);
        }

        Account account = JsonUtil.toBean(accountJson, Account.class);

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

//        int i = 1/0;

        if (!modify) {
            throw new RuntimeException("@@@ 修改订单状态失败");
        }

        return new MessageResponse(true, CommonResultCode.SUCCESS.message, CommonResultCode.SUCCESS.code);
    }


    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public MessageResponse tccOrder(Long storageId, Integer count) {

        String xid = RootContext.getXID();
        log.info("xid :{}", xid);


        AccountRemoteService accountRemoteService = RPCClient.getRemoteService(AccountRemoteService.class);
        String accountJson = accountRemoteService.getAccountById(1L);
        if (StringUtils.isBlank(accountJson)) {
            return new MessageResponse(false, CommonResultCode.SYSTEM_ERROR_INSERT_FAIL.message);
        }
        //获取库存
        StorageRemoteService storageRemoteService = RPCClient.getRemoteService(StorageRemoteService.class);
        String storageJson = storageRemoteService.getStorage(storageId);
        if (StringUtils.isBlank(storageJson)) {
            return new MessageResponse(false, CommonResultCode.SYSTEM_ERROR_INSERT_FAIL.message);
        }

        Account account = JsonUtil.toBean(accountJson, Account.class);

        Storage storage = JsonUtil.toBean(storageJson, Storage.class);

        //判断是否能下单
        BigDecimal residue = account.getResidue();
        BigDecimal price = storage.getPrice();
        BigDecimal totalMoney = price.multiply(new BigDecimal(count));
        if (residue.compareTo(totalMoney) < 0 || storage.getResidue() < count) {
            return new MessageResponse(false, CommonResultCode.SYSTEM_ERROR_INSERT_FAIL.message);
        }

        //下单
        Order order = new Order(1L, storageId, count, totalMoney, false, new Date());
        Long orderNum = insertReturnPk(order);
        if (orderNum == null) {
            return new MessageResponse(false, CommonResultCode.SYSTEM_ERROR_INSERT_FAIL.message);
        }

        order.setId(orderNum);
        tccOderService.order(storageId, count, totalMoney, account, storage, accountJson, storageJson,  order);

        return new MessageResponse(true, CommonResultCode.SUCCESS.message, CommonResultCode.SUCCESS.code);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageResponse mqOrderDemo(Long storageId, Integer count) {
        //获取账号信息.
        AccountRemoteService accountRemoteService = RPCClient.getRemoteService(AccountRemoteService.class);
        String accountJson = accountRemoteService.getAccountById(1L);
        Account account = JsonUtil.toBean(accountJson, Account.class);
        //获取库存信息
        StorageRemoteService storageRemoteService = RPCClient.getRemoteService(StorageRemoteService.class);
        String storageJson = storageRemoteService.getStorage(storageId);
        Storage storage = JsonUtil.toBean(storageJson, Storage.class);
        //判断是否可以下单
        BigDecimal residue = account.getResidue();
        BigDecimal price = storage.getPrice();
        BigDecimal totalMoney = price.multiply(new BigDecimal(count));
        if (residue.compareTo(totalMoney) < 0 || storage.getResidue() < count) {
            return new MessageResponse(false, "Insufficient account balance.");
        }
        //下单
        Order order = new Order(1L, storageId, count, totalMoney, false, new Date());
        Long orderNum = insertReturnPk(order);
        if (orderNum == null) {
            return new MessageResponse(false, CommonResultCode.SYSTEM_ERROR_INSERT_FAIL.message);
        }

        //自定义消息id
        String messageId = ProjectSnowflakeIdWorker.getInstance().nextId() + "";
        //构建本地消息entity
        //本地消息表存一条消息. 由于和下单是同个库 因此可以被同一个事务控制.
        OrderMessageRecord orderMessageRecord = new OrderMessageRecord(orderNum, messageId, 0, false);
        boolean preCommit = messageTransactionRecordService.preCommit(orderMessageRecord);
        if (!preCommit) {
            //直接抛出异常. 回滚事务
            throw new MessageMqException("preCommit message failure. message: " + JsonUtil.toJson(orderMessageRecord));
        }
        //将消息关联的库存信息放到redis
        LettuceRedis.getInstance().set(messageId, storage, BaseMathConstants.ONE_HOUR_4MILLISECONDS);
        //投递消息到mq
        boolean commit = messageTransactionRecordService.commit(messageId, true);
        if (!commit) {
            //直接抛出异常. 回滚事务
            throw new MessageMqException("commit message failure. message: " + JsonUtil.toJson(orderMessageRecord));
        }
        return new MessageResponse(true, CommonResultCode.SUCCESS.message);
    }
}
