package com.hqy.cloud.db.common;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hqy.cloud.common.result.PageResult;

import java.util.function.Function;

/**
 * @author qiyuan.hong
 * @date 2024/8/1
 */
public class PageWrappers {

    public static <T> PageResult<T> buildResult(int currentPage, Page<T> page) {
        return new PageResult<>(currentPage, page.getTotal(), (int) page.getPages(), page.getRecords());
    }


    public static <T, R> PageResult<R> buildResult(int currentPage, Page<T> page, Function<T, R> function) {
        return new PageResult<>(currentPage, page.getTotal(), (int) page.getPages(), page.getRecords().stream().map(function).toList());
    }

}
