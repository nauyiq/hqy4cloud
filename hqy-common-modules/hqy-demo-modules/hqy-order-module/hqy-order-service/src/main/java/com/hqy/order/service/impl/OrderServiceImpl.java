package com.hqy.order.service.impl;

import com.hqy.base.common.bind.MessageResponse;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.common.dto.OrderDetailDTO;
import com.hqy.common.entity.account.Wallet;
import com.hqy.common.entity.order.Order;
import com.hqy.common.entity.storage.Storage;
import com.hqy.common.service.StorageRemoteService;
import com.hqy.common.service.WalletRemoteService;
import com.hqy.order.service.OrderService;
import com.hqy.order.service.OrderTkService;
import com.hqy.order.service.TccOderService;
import com.hqy.rpc.nacos.client.starter.RPCClient;
import com.hqy.util.AssertUtil;
import com.hqy.util.JsonUtil;
import com.hqy.util.identity.ProjectSnowflakeIdWorker;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 10:46
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderTkService orderTkService;

    private final TccOderService tccOderService;



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
    public MessageResponse seataATOrder(Long storageId, Integer count, Long accountId) {
        //seata事务在微服务进行传播 最主要的依赖就是全局事务id xid.
        log.info("Seata TM start global transaction, xid:{}", RootContext.getXID());

        //校验订单
        Wallet wallet = getWallet(accountId);
        Storage storage = getStorage(storageId);
        OrderDetailDTO detail = new OrderDetailDTO(wallet, storage, count, storage.getStorage());
        if (!detail.enableOrder()) {
            return CommonResultCode.messageResponse(CommonResultCode.INVALID_DATA);
        }

        //下单
        Order order = new Order(1L, storageId, count, detail.totalMoney, false, new Date());
        order.setId(ProjectSnowflakeIdWorker.getInstance().nextId());
        if (!orderTkService.insert(order)) {
            return CommonResultCode.messageResponse(CommonResultCode.INVALID_DATA);
        }

        //RPC减库存
        storage.setStorage(storage.getStorage() - count);
        if (!modifyStorage(storage)) {
            return CommonResultCode.messageResponse(CommonResultCode.INVALID_DATA);
        }

        //RPC减账户余额
        wallet.setMoney(wallet.getMoney().subtract(detail.totalMoney));
        AssertUtil.isTrue(modifyAccount(wallet), "修改账户余额失败");

        //修改订单状态
        order.setId(order.getId());
        order.setStatus(true);
        AssertUtil.isTrue(orderTkService.update(order), "修改订单状态失败");
        return CommonResultCode.messageResponse();
    }


    @Override
    @GlobalTransactional(name = "seataTccOrder", rollbackFor = Exception.class)
    public MessageResponse seataTccOrder(Long storageId, Integer count, Long accountId) {

        //seata事务在微服务进行传播 最主要的依赖就是全局事务id xid.
        log.info("Seata TM start global transaction, xid:{}", RootContext.getXID());

        //校验订单
        Wallet wallet = getWallet(accountId);
        Storage storage = getStorage(storageId);
        OrderDetailDTO detail = new OrderDetailDTO(wallet, storage, count, storage.getStorage());
        if (!detail.enableOrder()) {
            return CommonResultCode.messageResponse(CommonResultCode.INVALID_DATA);
        }

        //生成order
        Order order = new Order(1L, storageId, count, detail.totalMoney, false, new Date());
        long orderId = ProjectSnowflakeIdWorker.getInstance().nextId();
        order.setId(orderId);
        AssertUtil.isTrue(tccOderService.order(count, detail.totalMoney, wallet, storage,  order), "tcc order failure.");
        return CommonResultCode.messageResponse();
    }


    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public MessageResponse rabbitmqLocalMessageOrder(Long storageId, Integer count, Long accountId) {
        //校验订单
        /*Wallet wallet = getWallet(accountId);
        Storage storage = getStorage(storageId);
        OrderDetailDTO detail = new OrderDetailDTO(wallet, storage, count, storage.getStorage());
        if (!detail.enableOrder()) {
            return CommonResultCode.messageResponse(CommonResultCode.INVALID_DATA);
        }
        //下单
        Order order = new Order(1L, storageId, count, detail.totalMoney, false, new Date());
        order.setId(ProjectSnowflakeIdWorker.getInstance().nextId());
        AssertUtil.isTrue(orderTkService.insert(order), "新增订单失败.");

        //构建本地消息 往本地消息表生成一条记录
        String messageId = ProjectSnowflakeIdWorker.getInstance().nextId() + "";
        OrderMessageRecord orderMessageRecord = new OrderMessageRecord(order.getId(), messageId, 0, false);
        RabbitmqMessageTransactionRecordServiceImpl service = SpringContextHolder.getBean(RabbitmqMessageTransactionRecordServiceImpl.class);

        //断言是否预提交成功 --> 往本地消息表存一条数据
        AssertUtil.isTrue(service.preCommit(orderMessageRecord), "preCommit message failure. message: " + JsonUtil.toJson(orderMessageRecord));
        //将消息关联的库存信息放到redis
        LettuceRedis.getInstance().set(messageId, storage, BaseMathConstants.ONE_HOUR_4MILLISECONDS);
        //断言发消息到rabbitmq是否成功
        AssertUtil.isTrue(service.commit(messageId, true), "commit message failure. message: " + JsonUtil.toJson(orderMessageRecord));*/
        return CommonResultCode.messageResponse();
    }




    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageResponse kafkaOrder(Long storageId, Integer count) {
        /*//获取账号信息.
        Account account = getWallet();
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
        }*/
        return new MessageResponse(true, CommonResultCode.SUCCESS.message);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageResponse rocketMqOrder(Long storageId, Integer count) {
     /*   //获取账号信息.
        Account account = getWallet(accountId);
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
        }*/


        return null;
    }



    private Storage getStorage(Long storageId) {
        StorageRemoteService storageRemoteService = RPCClient.getRemoteService(StorageRemoteService.class);
        String storageJson = storageRemoteService.getStorage(storageId);
        return JsonUtil.toBean(storageJson, Storage.class);
    }

    private Wallet getWallet(Long accountId) {
        WalletRemoteService walletRemoteService = RPCClient.getRemoteService(WalletRemoteService.class);
        String walletInfo = walletRemoteService.walletInfo(accountId);
        return JsonUtil.toBean(walletInfo, Wallet.class);
    }

    private boolean modifyStorage(Storage storage) {
        StorageRemoteService service = RPCClient.getRemoteService(StorageRemoteService.class);
        return service.modifyStorage(JsonUtil.toJson(storage));
    }

    private boolean modifyAccount(Wallet wallet) {
        WalletRemoteService service = RPCClient.getRemoteService(WalletRemoteService.class);
        return service.modifyAccount(JsonUtil.toJson(wallet));
    }
}
