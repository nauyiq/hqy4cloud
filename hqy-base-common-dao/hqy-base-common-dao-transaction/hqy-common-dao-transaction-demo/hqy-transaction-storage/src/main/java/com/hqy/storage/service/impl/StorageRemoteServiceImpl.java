package com.hqy.storage.service.impl;

import com.hqy.order.common.entity.Storage;
import com.hqy.order.common.service.StorageRemoteService;
import com.hqy.rpc.api.AbstractRPCService;
import com.hqy.storage.service.StorageService;
import com.hqy.util.JsonUtil;
import org.springframework.stereotype.Service;

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

    @Override
    public String getStorage(Long storageId) {
        if (storageId == null) {
            return "";
        }
        Storage storage = storageService.queryById(storageId);
        return JsonUtil.toJson(storage);
    }


    @Override
    public boolean modifyStorage(String storage) {
        Storage bean = JsonUtil.toBean(storage, Storage.class);
        if (Objects.isNull(bean)) {
            return false;
        }
        return storageService.update(bean);
    }
}
