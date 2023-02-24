package com.hqy.cloud.common.vo.menu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/12 10:46
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AdminMenuInfoVO extends BaseMenuVO {

    /**
     * 子菜单
     */
    private List<BaseMenuVO> children;

}
