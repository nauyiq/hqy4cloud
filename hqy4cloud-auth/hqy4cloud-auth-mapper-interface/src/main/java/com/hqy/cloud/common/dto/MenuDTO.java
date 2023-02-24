package com.hqy.cloud.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/19 16:56
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuDTO {

    private Long id;
    @NotEmpty(message = "Menu name should not be empty.")
    private String name;
    private String path;
    private Long parentId;
    @NotNull(message = "Menu type should not be null.")
    private Integer menuType;
    private String icon;
    private Integer status;
    private String permission;
    @NotNull(message = "Menu sort order should not be null.")
    private Integer sortOrder;


}
