package com.hqy.cloud.tk.support;

import com.hqy.cloud.common.result.CommonResultCode;
import com.hqy.cloud.tk.PrimaryLessBaseEntity;
import com.hqy.cloud.tk.PrimaryLessTkMapper;
import com.hqy.cloud.tk.PrimaryLessTkService;
import com.hqy.util.AssertUtil;

import java.util.List;

/**
 * @see PrimaryLessTkService
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/9 9:40
 */
public abstract class PrimaryLessTkServiceImpl<T extends PrimaryLessBaseEntity> implements PrimaryLessTkService<T> {

    private PrimaryLessTkMapper<T> checkDao() {
        PrimaryLessTkMapper<T> dao = getTkDao();
        AssertUtil.notNull(dao, "base dao注入异常, 请检查配置.");
        return dao;
    }

    @Override
    public T queryOne(T t) {
        PrimaryLessTkMapper<T> dao= checkDao();
        return dao.selectOne(t);
    }

    @Override
    public List<T> queryList(T t) {
        PrimaryLessTkMapper<T> dao= checkDao();
        return dao.select(t);
    }

    @Override
    public List<T> queryAll() {
        PrimaryLessTkMapper<T> dao= checkDao();
        return dao.selectAll();
    }

    @Override
    public boolean insert(T t) {
        AssertUtil.notNull(t, CommonResultCode.INVALID_DATA.message);
        PrimaryLessTkMapper<T> dao= checkDao();
        return dao.insert(t) > 0;
    }

    @Override
    public boolean insertList(List<T> entities) {
        AssertUtil.notEmpty(entities, CommonResultCode.INVALID_DATA.message);
        PrimaryLessTkMapper<T> dao= checkDao();
        return dao.insertList(entities) > 0;
    }

    @Override
    public boolean update(T t) {
        AssertUtil.notNull(t, CommonResultCode.INVALID_DATA.message);
        PrimaryLessTkMapper<T> dao = checkDao();
        return dao.updateByPrimaryKey(t) > 0;
    }

    @Override
    public boolean updateSelective(T t) {
        AssertUtil.notNull(t, CommonResultCode.INVALID_DATA.message);
        PrimaryLessTkMapper<T> dao = checkDao();
        return dao.updateByPrimaryKeySelective(t) > 0;
    }

    @Override
    public boolean delete(T t) {
        PrimaryLessTkMapper<T> dao = checkDao();
        return dao.delete(t) > 0;
    }

}
