package com.hqy.cloud.auth.base.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/11/15 11:12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleOnResourcesDTO {

    private String roleName;
    private List<ResourceDTO> resources;


}
