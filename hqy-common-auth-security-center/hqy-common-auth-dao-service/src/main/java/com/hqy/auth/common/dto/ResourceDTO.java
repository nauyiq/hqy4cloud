package com.hqy.auth.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/21 11:29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceDTO {

    private Integer id;
    @NotEmpty(message = "Resource name should not be empty.")
    private String name;
    @NotEmpty(message = "Resource path should not be empty.")
    private String path;
    private String method;
    private String permission;
    @NotNull(message = "Resource status should not be null.")
    private Boolean status;


}
