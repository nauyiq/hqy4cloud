package com.hqy.cloud.db.mybatisplus;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/2/29
 */
public abstract class BasePlusServiceImpl<T extends BaseEntity, M extends BasePlusMapper<T>> extends ServiceImpl<M, T> implements BasePlusService<T> {

}
