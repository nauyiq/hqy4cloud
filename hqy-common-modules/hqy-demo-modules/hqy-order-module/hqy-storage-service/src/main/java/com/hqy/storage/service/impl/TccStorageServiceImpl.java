package com.hqy.storage.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hqy.order.common.entity.Storage;
import com.hqy.storage.service.StorageTkService;
import com.hqy.storage.service.TccStorageService;
import com.hqy.util.AssertUtil;
import io.seata.rm.tcc.api.BusinessActionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/29 15:16
 */
@Slf4j
@Service
public class TccStorageServiceImpl implements TccStorageService {

    @Resource
    private StorageTkService storageTkService;

    /**
     * 用于标记库存交易在当前tcc事务中是否进行过空回滚 防止悬挂。
     */
    private static final Cache<String, Boolean> BLANK_ROLLBACK_CACHE =
            CacheBuilder.newBuilder().initialCapacity(256).expireAfterAccess(1, TimeUnit.HOURS).build();

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean modifyStorage(Storage beforeStorage, Storage afterStorage) {
        //防悬挂控制
        if (Boolean.TRUE.equals(BLANK_ROLLBACK_CACHE.getIfPresent(beforeStorage.getId() + ""))) {
            return false;
        }
        //TODO 正常下单业务中 这种更新库存动作必须加锁 可以使乐观锁 或 悲观锁
        AssertUtil.isTrue(storageTkService.update(afterStorage), "更新库存失败.");
        return true;
    }

    @Override
    public boolean commitTcc(BusinessActionContext context) {
        return true;
    }

    @Override
    public boolean cancel(BusinessActionContext context) {
        try {
            JSONObject beforeStorage = (JSONObject) context.getActionContext("beforeStorage");
            Storage storage = beforeStorage.toJavaObject(Storage.class);
            //查询库存在不在
            Storage storageFromDb = storageTkService.queryById(storage.getId());
            if (Objects.isNull(storageFromDb)) {
                //标记已进行空回滚 悬挂控制
                BLANK_ROLLBACK_CACHE.put(storage.getId() + "", true);
                //空回滚
                return true;
            }
            //TODO 正常下单业务中 这种更新库存动作必须加锁 可以使乐观锁 或 悲观锁
            return storageTkService.update(storage);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //正常来说应该控制 重试次数... demo不控制了....
            return false;
        }
    }
}
