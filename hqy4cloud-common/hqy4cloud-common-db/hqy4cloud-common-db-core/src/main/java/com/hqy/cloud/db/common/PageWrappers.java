package com.hqy.cloud.db.common;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hqy.cloud.common.result.PageResult;

import java.util.function.Function;

/**
 * @author qiyuan.hong
 * @date 2024/8/1
 */
public class PageWrappers {

    public static <T> PageResult<T> buildResult(Page<T> page) {
        return new PageResult<>((int) page.getCurrent(), (int) page.getSize(), page.getTotal(), page.getRecords());
    }

    public static <T, R>PageResult<R> buildResult(Page<T> page, Function<T, R> function) {
        return new PageResult<>((int) page.getCurrent(), page.getTotal(), (int) page.getPages(), page.getRecords().stream().map(function).toList());
    }

}
