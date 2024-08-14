package com.hqy.cloud.common.request;

import lombok.Getter;
import lombok.Setter;

/**
 * @author qiyuan.hong
 * @date 2024/8/1
 */
@Getter
@Setter
public class PageRequest extends BaseRequest {

    /**
     * 当前页
     */
    private int currentPage;
    /**
     * 每页结果数
     */
    private int pageSize;

}
