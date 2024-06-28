package com.hqy.cloud.auth.base.dto;

import com.hqy.cloud.auth.account.entity.Permissions;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
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
    @NotNull(message = "Resource status should not be null.")
    private Boolean status;

    public ResourceDTO(Integer id, String path, String method) {
        this.id = id;
        this.path = path;
        this.method = method;
    }

    public ResourceDTO(Integer id, String path) {
        this.id = id;
        this.path = path;
    }

    public ResourceDTO(Permissions permissions) {
        this.id = permissions.getId();
        this.name = permissions.getName();
        this.path = permissions.getPath();
        this.method = permissions.getMethod();
        this.status = permissions.getStatus();
    }
}
