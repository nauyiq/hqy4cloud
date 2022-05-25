package com.hqy.storage.service.impl;

import com.hqy.base.BaseDao;
import com.hqy.base.impl.BaseTkServiceImpl;
import com.hqy.common.entity.storage.Storage;
import com.hqy.storage.dao.StorageDao;
import com.hqy.storage.service.StorageTkService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 14:15
 */
@Service
public class StorageTkServiceImpl extends BaseTkServiceImpl<Storage, Long> implements StorageTkService {

    @Resource
    private StorageDao storageDao;

    @Override
    public BaseDao<Storage, Long> selectDao() {
        return storageDao;
    }


    @Override
    public boolean casUpdate(Long productId, int use, int residue, Integer beforeResidue) {
        return storageDao.casUpdate(productId, use, residue, beforeResidue) > 0;
    }
}
