package com.hqy.foundation.common;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/20 10:34
 */
public interface EventContent {

    /**
     * 获取内容id， 唯一标识 可用于区分幂等、或超限判断等.
     * @return content id
     */
    @JsonIgnore
    String getId();

}
