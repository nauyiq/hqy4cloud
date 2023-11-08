package com.hqy.cloud.elasticsearch.common;

import com.hqy.cloud.common.result.PageResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/10/18 15:36
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageSearchAfterResult<T> {

    private PageResult<T> result;
    private List<Object> afterResult;


}
