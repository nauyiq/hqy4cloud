package com.hqy.cloud.db.tk;

import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;
import tk.mybatis.mapper.common.base.BaseDeleteMapper;
import tk.mybatis.mapper.common.base.BaseInsertMapper;
import tk.mybatis.mapper.common.base.BaseSelectMapper;
import tk.mybatis.mapper.common.base.BaseUpdateMapper;

/**
 * 只创建对表的增删改操作
 * 使用TKMapper通用Mapper时一定声明主键，否则selectByPrimaryKey会查询所有的实体参数，
 * 一定要导入import javax.persistence.Id;不要导成import org.springframework.data.annotation.Id
 * @author qy
 * @date 2021-07-16 11:22
 */
public interface BaseTkMapper<T, Pk> extends
        BaseInsertMapper<T>,
        BaseUpdateMapper<T>,
        BaseDeleteMapper<T>,
        BaseSelectMapper<T>,
        IdsMapper<T>,
        MySqlMapper<T>,
        Mapper<T> {
}
