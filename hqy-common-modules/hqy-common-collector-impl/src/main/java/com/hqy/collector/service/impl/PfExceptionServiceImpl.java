package com.hqy.collector.service.impl;

import com.hqy.base.BaseDao;
import com.hqy.base.impl.BaseTkServiceImpl;
import com.hqy.collector.dao.PfExceptionDao;
import com.hqy.collector.service.PfExceptionService;
import com.hqy.coll.struct.PfException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/7 14:31
 */
@Service
public class PfExceptionServiceImpl extends BaseTkServiceImpl<PfException, Long> implements PfExceptionService {

    @Resource
    private PfExceptionDao pfExceptionDao;

    @Override
    public BaseDao<PfException, Long> selectDao() {
        return pfExceptionDao;
    }
}
