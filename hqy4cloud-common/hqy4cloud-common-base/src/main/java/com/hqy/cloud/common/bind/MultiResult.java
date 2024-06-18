package com.hqy.cloud.common.bind;

import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.common.result.Result;
import com.hqy.cloud.common.result.ResultCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author qiyuan.hong
 * @date 2024/7/18
 */
@Getter
@Setter
public class MultiResult<T> extends Response {

    /**
     * data
     */
    private List<T> data;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页码
     */
    private int page;

    /**
     * 每页记录数
     */
    private int size;

    public MultiResult(boolean result, int code, String message, List<T> data, long total, int page, int size) {
        super(result, message, code);
        this.data = data;
        this.total = total;
        this.page = page;
        this.size = size;
    }

    public static <T> MultiResult<T> ok(List<T> data, long total, int page, int size) {
        return new MultiResult<T>(true, ResultCode.SUCCESS.code, ResultCode.SUCCESS.message, data, total, page, size);
    }

    public static <T> MultiResult<T> failed(Result result) {
        return new MultiResult<>(false, result.getCode(), result.getMessage(), null, 0, 0, 0);
    }

    public static <T> MultiResult<T> convert(int size, PageResult<T> pageResult) {
        return MultiResult.ok(pageResult.getResultList(), pageResult.getTotal(), pageResult.getCurrentPage(), size);
    }



}
