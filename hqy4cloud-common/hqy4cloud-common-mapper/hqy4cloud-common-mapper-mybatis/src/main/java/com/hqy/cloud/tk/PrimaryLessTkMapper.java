package com.hqy.cloud.tk;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;
import tk.mybatis.mapper.common.base.BaseDeleteMapper;
import tk.mybatis.mapper.common.base.BaseInsertMapper;
import tk.mybatis.mapper.common.base.BaseSelectMapper;
import tk.mybatis.mapper.common.base.BaseUpdateMapper;

/**
 * 无主键策略的TK MAPPER.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/9 9:38
 */
public interface PrimaryLessTkMapper<T> extends
        BaseInsertMapper<T>,
        BaseUpdateMapper<T>,
        BaseDeleteMapper<T>,
        BaseSelectMapper<T>,
        MySqlMapper<T>,
        Mapper<T> {
}
