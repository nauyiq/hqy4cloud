package com.hqy.storage.service.impl;

import com.hqy.order.common.entity.Storage;
import com.hqy.order.common.service.StorageRemoteService;
import com.hqy.rpc.api.AbstractRPCService;
import com.hqy.storage.service.StorageService;
import com.hqy.storage.service.TccStorageService;
import com.hqy.util.JsonUtil;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 14:17
 */
@Service
public class StorageRemoteServiceImpl extends AbstractRPCService implements StorageRemoteService {

    @Resource
    private StorageService storageService;

    @Resource
    private TccStorageService tccStorageService;

    @Override
    public String getStorage(Long storageId) {
        if (storageId == null) {
            return "";
        }
        Storage storage = storageService.queryById(storageId);
        return JsonUtil.toJson(storage);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean modifyStorage(String storage) {
        String xid = RootContext.getXID();
        System.out.println(xid);
        Storage bean = JsonUtil.toBean(storage, Storage.class);
        if (Objects.isNull(bean)) {
            return false;
        }
        return storageService.update(bean);
    }

    @Override
    public boolean tccModifyStorage(String beforeStorage, String afterStorage) {
        return tccStorageService.modifyStorage(JsonUtil.toBean(beforeStorage, Storage.class), JsonUtil.toBean(afterStorage, Storage.class));
    }
}
