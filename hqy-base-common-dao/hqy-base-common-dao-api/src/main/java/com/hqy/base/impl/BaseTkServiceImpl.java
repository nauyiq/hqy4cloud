package com.hqy.base.impl;

import com.hqy.base.BaseDao;
import com.hqy.base.BaseEntity;
import com.hqy.base.BaseTkService;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.util.AssertUtil;

import java.util.Date;
import java.util.List;

/**
 * 基础的crud单表逻辑 基于tk实现
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/1 17:47
 */
public abstract class BaseTkServiceImpl<T extends BaseEntity<PK>, PK> implements BaseTkService<T, PK> {

    private BaseDao<T, PK> checkDao() {
        BaseDao<T, PK> dao = getTkDao();
        AssertUtil.notNull(dao, "base dao注入异常, 请检查配置.");
        return dao;
    }


    @Override
    public T queryById(PK pk) {
        BaseDao<T, PK> dao = checkDao();
        return dao.selectByPrimaryKey(pk);
    }

    @Override
    public T queryOne(T t) {
        BaseDao<T, PK> dao = checkDao();
        return dao.selectOne(t);
    }

    @Override
    public List<T> queryList(T t) {
        BaseDao<T, PK> dao = checkDao();
        return dao.select(t);
    }

    @Override
    public List<T> queryAll() {
        BaseDao<T, PK> dao = checkDao();
        return dao.selectAll();
    }

    @Override
    public boolean insert(T t) {
        AssertUtil.notNull(t, CommonResultCode.INVALID_DATA.message);
        BaseDao<T, PK> dao = checkDao();
        int i = dao.insert(t);
        return i > 0;
    }

    @Override
    public PK insertReturnPk(T t) {
        AssertUtil.notNull(t, CommonResultCode.INVALID_DATA.message);
        BaseDao<T, PK> dao = checkDao();
        dao.insert(t);
        return t.getId();
    }

    @Override
    public boolean insertList(List<T> entities) {
        AssertUtil.notEmpty(entities, CommonResultCode.INVALID_DATA.message);
        BaseDao<T, PK> dao = checkDao();
        int i = dao.insertList(entities);
        return i > 0;
    }

    @Override
    public boolean update(T t) {
        AssertUtil.notNull(t, CommonResultCode.INVALID_DATA.message);
        BaseDao<T, PK> dao = checkDao();
        t.setUpdated(new Date());
        return dao.updateByPrimaryKey(t) > 0;
    }

    @Override
    public boolean updateSelective(T t) {
        AssertUtil.notNull(t, CommonResultCode.INVALID_DATA.message);
        BaseDao<T, PK> dao = checkDao();
        t.setUpdated(new Date());
        return dao.updateByPrimaryKeySelective(t) > 0;
    }

    @Override
    public boolean deleteByPrimaryKey(PK pk) {
        BaseDao<T, PK> dao = checkDao();
        int i = dao.deleteByPrimaryKey(pk);
        return i > 0;
    }

    @Override
    public boolean delete(T t) {
        BaseDao<T, PK> dao = checkDao();
        return dao.delete(t) > 0;
    }
}
