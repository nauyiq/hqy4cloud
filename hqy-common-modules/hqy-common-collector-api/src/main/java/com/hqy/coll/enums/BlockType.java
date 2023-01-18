package com.hqy.coll.enums;

import lombok.AllArgsConstructor;

/**
 * 封禁类型
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/5 10:31
 */
@AllArgsConstructor
public enum BlockType {

    /**
     * bi分析封禁
     */
    BI_BLOCK(1),

    /**
     * 人工封禁
     */
    MANUAL_BLOCK(2),

    ;

    public final Integer code;


}
