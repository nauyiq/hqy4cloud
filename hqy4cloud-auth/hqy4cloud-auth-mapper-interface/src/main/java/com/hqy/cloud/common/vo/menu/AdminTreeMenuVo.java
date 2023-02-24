package com.hqy.cloud.common.vo.menu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/16 9:57
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AdminTreeMenuVo extends BaseMenuVO {

    /**
     * 子节点.
     */
    private List<AdminTreeMenuVo> children;

}
