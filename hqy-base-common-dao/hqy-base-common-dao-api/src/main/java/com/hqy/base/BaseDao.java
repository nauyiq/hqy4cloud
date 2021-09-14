package com.hqy.base;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;
import tk.mybatis.mapper.common.base.BaseDeleteMapper;
import tk.mybatis.mapper.common.base.BaseInsertMapper;
import tk.mybatis.mapper.common.base.BaseSelectMapper;
import tk.mybatis.mapper.common.base.BaseUpdateMapper;

import java.io.Serializable;

/**
 * 只创建对表的增删改操作
 * 使用TKMapper通用Mapper时一定声明主键，否则selectByPrimaryKey会查询所有的实体参数，
 * 一定要导入import javax.persistence.Id;不要导成import org.springframework.data.annotation.Id
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-07-16 11:22
 */
public interface BaseDao<T, Pk extends Serializable> extends
        BaseInsertMapper<T>,
        BaseUpdateMapper<T>,
        BaseDeleteMapper<T>,
        BaseSelectMapper<T>,
        MySqlMapper<T>,
        Mapper<T> {
}
