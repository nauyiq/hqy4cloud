package com.hqy.order.service.impl;

import com.hqy.base.BaseDao;
import com.hqy.base.common.base.lang.BaseMathConstants;
import com.hqy.base.common.bind.MessageResponse;
import com.hqy.base.common.exception.MessageMqException;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.base.common.swticher.CommonSwitcher;
import com.hqy.base.impl.BaseTkServiceImpl;
import com.hqy.fundation.cache.redis.LettuceRedis;
import com.hqy.order.common.dto.OrderDetailDTO;
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
import com.hqy.util.AssertUtil;
import com.hqy.util.JsonUtil;
import com.hqy.util.identity.ProjectSnowflakeIdWorker;
import com.hqy.util.spring.SpringContextHolder;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.json.Json;
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

    @Override
    public BaseDao<Order, Long> selectDao() {
        return orderDao;
    }

    /**
     * @GlobalTransactional seata开启分布式事务的全局入口 即TM开始全局事务。
     * timeoutMills 超时时间 默认60s
     * name         全局事务实例的给定名称 默认方法名
     * rollbackFor  与@Transactional 一样 捕获到什么异常而回滚。
     * @param  storageId 商品id
     * @param  count 数目
     * @return MessageResponse
     */
    @Override
    @GlobalTransactional(name = "seataATOrder", rollbackFor = Exception.class)
    public MessageResponse seataATOrder(Long storageId, Integer count) {

        //seata事务在微服务进行传播 最主要的依赖就是全局事务id xid.
        log.info("Seata TM start global transaction, xid:{}", RootContext.getXID());

        //校验订单
        Account account = getAccount();
        Storage storage = getStorage(storageId);
        OrderDetailDTO detail = new OrderDetailDTO(account, storage, count, storage.getResidue());
        if (!detail.enableOrder()) {
            return CommonResultCode.messageResponse(CommonResultCode.INVALID_DATA);
        }

        //下单
        Order order = new Order(1L, storageId, count, detail.totalMoney, false, new Date());
        if (!insert(order)) {
            return CommonResultCode.messageResponse(CommonResultCode.INVALID_DATA);
        }

        //RPC减库存
        storage.setUsed(storage.getUsed() + count);
        storage.setResidue(storage.getResidue() - count);
        if (!modifyStorage(storage)) {
            return CommonResultCode.messageResponse(CommonResultCode.INVALID_DATA);
        }

        //RPC减账户余额
        account.setUsed(account.getUsed().add(detail.totalMoney));
        account.setResidue(account.getResidue().subtract(detail.totalMoney));
        AssertUtil.isTrue(modifyAccount(account), "修改账户余额失败");

        //修改订单状态
        order.setId(order.getId());
        order.setStatus(true);
        AssertUtil.isTrue(update(order), "修改订单状态失败");
        return CommonResultCode.messageResponse();
    }


    @Override
    @GlobalTransactional(name = "seataTccOrder", rollbackFor = Exception.class)
    public MessageResponse seataTccOrder(Long storageId, Integer count) {

        //seata事务在微服务进行传播 最主要的依赖就是全局事务id xid.
        log.info("Seata TM start global transaction, xid:{}", RootContext.getXID());

        //校验订单
        Account account = getAccount();
        Storage storage = getStorage(storageId);
        OrderDetailDTO detail = new OrderDetailDTO(account, storage, count, storage.getResidue());
        if (!detail.enableOrder()) {
            return CommonResultCode.messageResponse(CommonResultCode.INVALID_DATA);
        }

        //生成order
        Order order = new Order(1L, storageId, count, detail.totalMoney, false, new Date());
        long orderId = ProjectSnowflakeIdWorker.getInstance().nextId();
        order.setId(orderId);

        order.setId(order.getId());
        tccOderService.order(count, detail.totalMoney, account, storage,  order);

        return new MessageResponse(true, CommonResultCode.SUCCESS.message, CommonResultCode.SUCCESS.code);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageResponse mqOrderDemo(Long storageId, Integer count) {
        /*//获取账号信息.
        Account account = getAccount();
        //获取库存信息
        Storage storage = getStorage(storageId);
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
        }*/
        return new MessageResponse(true, CommonResultCode.SUCCESS.message);
    }

    private Storage getStorage(Long storageId) {
        StorageRemoteService storageRemoteService = RPCClient.getRemoteService(StorageRemoteService.class);
        String storageJson = storageRemoteService.getStorage(storageId);
        return JsonUtil.toBean(storageJson, Storage.class);
    }

    private Account getAccount() {
        AccountRemoteService accountRemoteService = RPCClient.getRemoteService(AccountRemoteService.class);
        String accountJson = accountRemoteService.getAccountById(1L);
        return JsonUtil.toBean(accountJson, Account.class);
    }

    private boolean modifyStorage(Storage storage) {
        StorageRemoteService service = RPCClient.getRemoteService(StorageRemoteService.class);
        return service.modifyStorage(JsonUtil.toJson(storage));
    }

    private boolean modifyAccount(Account account) {
        AccountRemoteService service = RPCClient.getRemoteService(AccountRemoteService.class);
        return service.modifyAccount(JsonUtil.toJson(account));
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageResponse kafkaOrder(Long storageId, Integer count) {
        //获取账号信息.
        Account account = getAccount();
        //获取库存信息
        Storage storage = getStorage(storageId);
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

        KafkaMessageTransactionRecordServiceImpl kafkaMessageTransactionRecordService
                = SpringContextHolder.getBean(KafkaMessageTransactionRecordServiceImpl.class);

        //本地消息表存一条消息. 由于和下单是同个库 因此可以被同一个事务控制.
        OrderMessageRecord orderMessageRecord = new OrderMessageRecord(orderNum, messageId, 0, false);
        boolean preCommit = kafkaMessageTransactionRecordService.preCommit(orderMessageRecord);
        if (!preCommit) {
            //直接抛出异常. 回滚事务
            throw new MessageMqException("preCommit message failure. message: " + JsonUtil.toJson(orderMessageRecord));
        }
        //将消息关联的库存信息放到redis
        LettuceRedis.getInstance().set(messageId, storage, BaseMathConstants.ONE_HOUR_4MILLISECONDS);
        //投递消息到mq
        boolean commit = kafkaMessageTransactionRecordService.commit(messageId, true);

        if (!commit) {
            //直接抛出异常. 回滚事务
            throw new MessageMqException("commit message failure. message: " + JsonUtil.toJson(orderMessageRecord));
        }
        return new MessageResponse(true, CommonResultCode.SUCCESS.message);

    }


    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageResponse rocketMqOrder(Long storageId, Integer count) {
        //获取账号信息.
        Account account = getAccount();
        //获取库存信息
        Storage storage = getStorage(storageId);
        //判断是否可以下单
        BigDecimal residue = account.getResidue();
        BigDecimal price = storage.getPrice();
        BigDecimal totalMoney = price.multiply(new BigDecimal(count));
        if (residue.compareTo(totalMoney) < 0 || storage.getResidue() < count) {
            return CommonResultCode.messageResponse(CommonResultCode.INVALID_DATA);
        }

        Order order = new Order(1L, storageId, count, totalMoney, false, new Date());
        if (!insert(order)) {
            return CommonResultCode.messageResponse(CommonResultCode.SYSTEM_ERROR_INSERT_FAIL);
        }



        return null;
    }

}
