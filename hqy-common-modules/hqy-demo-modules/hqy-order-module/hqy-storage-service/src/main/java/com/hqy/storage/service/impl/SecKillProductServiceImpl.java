package com.hqy.storage.service.impl;

import com.hqy.base.BaseDao;
import com.hqy.base.impl.BaseTkServiceImpl;
import com.hqy.common.entity.storage.SecKillProduct;
import com.hqy.storage.dao.SecKillProductDao;
import com.hqy.storage.service.SecKillProductService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author qiyuan.hong
 * @date 2022-05-25 23:45
 */
@Service
public class SecKillProductServiceImpl extends BaseTkServiceImpl<SecKillProduct, Long> implements SecKillProductService {

    @Resource
    private SecKillProductDao dao;

    @Override
    public BaseDao<SecKillProduct, Long> getTkDao() {
        return dao;
    }
}
