package com.hqy.cloud.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/15 16:37
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {

    private Integer id;
    private String name;
    private Integer level;
    private String note;


}
