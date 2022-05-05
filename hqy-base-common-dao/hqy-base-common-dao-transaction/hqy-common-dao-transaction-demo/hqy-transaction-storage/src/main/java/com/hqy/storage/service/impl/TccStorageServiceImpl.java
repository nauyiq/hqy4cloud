package com.hqy.storage.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hqy.order.common.entity.Storage;
import com.hqy.storage.service.StorageService;
import com.hqy.storage.service.TccStorageService;
import io.seata.rm.tcc.api.BusinessActionContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/29 15:16
 */
@Service
public class TccStorageServiceImpl implements TccStorageService {

    @Resource
    private StorageService storageService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean modifyStorage(Storage beforeStorage, Storage afterStorage) {
        boolean update = storageService.update(afterStorage);
        if (update) {
            return true;
        }
        throw new RuntimeException("更新库存失败.");
    }

    @Override
    public boolean commitTcc(BusinessActionContext context) {
        return true;
    }

    @Override
    public boolean cancel(BusinessActionContext context) {
        JSONObject beforeStorage = (JSONObject) context.getActionContext("beforeStorage");
        return storageService.update(beforeStorage.toJavaObject(Storage.class));
    }
}
