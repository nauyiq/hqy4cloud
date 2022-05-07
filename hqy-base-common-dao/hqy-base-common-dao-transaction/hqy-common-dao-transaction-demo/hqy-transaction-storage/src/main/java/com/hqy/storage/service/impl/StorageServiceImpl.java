package com.hqy.storage.service.impl;

import com.hqy.base.BaseDao;
import com.hqy.base.impl.BaseTkServiceImpl;
import com.hqy.order.common.entity.Storage;
import com.hqy.storage.dao.StorageDao;
import com.hqy.storage.service.StorageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 14:15
 */
@Service
public class StorageServiceImpl extends BaseTkServiceImpl<Storage, Long> implements StorageService {

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
