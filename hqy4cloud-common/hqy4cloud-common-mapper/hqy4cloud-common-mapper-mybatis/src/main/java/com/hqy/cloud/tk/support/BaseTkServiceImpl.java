package com.hqy.cloud.tk.support;

import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.tk.BaseTkMapper;
import com.hqy.cloud.tk.BaseTkService;
import com.hqy.cloud.tk.model.BaseEntity;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.ReflectUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 基础的crud单表逻辑 基于tk实现
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/1 17:47
 */
public abstract class BaseTkServiceImpl<T extends BaseEntity<PK>, PK> implements BaseTkService<T, PK> {

    private BaseTkMapper<T, PK> checkMapper() {
        BaseTkMapper<T, PK> tkMapper = getTkMapper();
        AssertUtil.notNull(tkMapper, "Base tk mapper注入异常, 请检查配置.");
        return tkMapper;
    }


    @Override
    public T queryById(PK pk) {
        BaseTkMapper<T, PK> dao = checkMapper();
        return dao.selectByPrimaryKey(pk);
    }

    @Override
    public T queryOne(T t) {
        BaseTkMapper<T, PK> dao = checkMapper();
        return dao.selectOne(t);
    }

    @Override
    public List<T> queryByIds(List<PK> pks) {
        return queryByIds("id", pks);
    }

    @Override
    public List<T> queryByIds(String pkName, List<PK> pks) {
        BaseTkMapper<T, PK> dao = checkMapper();
        Class<T> targetGenericClass = ReflectUtils.getTargetGenericClass(getClass(), 0);
        Example example = new Example(targetGenericClass);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn(pkName, pks);
        return dao.selectByExample(example);
    }

    @Override
    public List<T> queryList(T t) {
        BaseTkMapper<T, PK> dao = checkMapper();
        return dao.select(t);
    }

    @Override
    public List<T> queryByExample(Example example) {
        BaseTkMapper<T, PK> mapper = checkMapper();
        return mapper.selectByExample(example);
    }

    @Override
    public List<T> queryAll() {
        BaseTkMapper<T, PK> dao = checkMapper();
        return dao.selectAll();
    }

    @Override
    public boolean insert(T t) {
        AssertUtil.notNull(t, ResultCode.INVALID_DATA.message);
        Date now = new Date();
        if (Objects.isNull(t.getCreated())) {
            t.setCreated(now);
        }
        if (Objects.isNull(t.getUpdated())) {
            t.setUpdated(now);
        }
        BaseTkMapper<T, PK> dao = checkMapper();
        int i = dao.insert(t);
        return i > 0;
    }

    @Override
    public PK insertReturnPk(T t) {
        AssertUtil.notNull(t, ResultCode.INVALID_DATA.message);
        BaseTkMapper<T, PK> dao = checkMapper();
        dao.insert(t);
        return t.getId();
    }

    @Override
    public boolean insertList(List<T> entities) {
        AssertUtil.notEmpty(entities, ResultCode.INVALID_DATA.message);
        BaseTkMapper<T, PK> dao = checkMapper();
        int i = dao.insertList(entities);
        return i > 0;
    }

    @Override
    public boolean update(T t) {
        AssertUtil.notNull(t, ResultCode.INVALID_DATA.message);
        BaseTkMapper<T, PK> dao = checkMapper();
        t.setUpdated(new Date());
        return dao.updateByPrimaryKey(t) > 0;
    }

    @Override
    public boolean updateSelective(T t) {
        AssertUtil.notNull(t, ResultCode.INVALID_DATA.message);
        BaseTkMapper<T, PK> dao = checkMapper();
        t.setUpdated(new Date());
        return dao.updateByPrimaryKeySelective(t) > 0;
    }

    @Override
    public boolean deleteByPrimaryKey(PK pk) {
        BaseTkMapper<T, PK> dao = checkMapper();
        int i = dao.deleteByPrimaryKey(pk);
        return i > 0;
    }

    @Override
    public boolean delete(T t) {
        BaseTkMapper<T, PK> dao = checkMapper();
        return dao.delete(t) > 0;
    }
}
