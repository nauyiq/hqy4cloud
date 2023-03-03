package com.hqy.cloud.auth.base.dto;

import lombok.Data;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/27 13:53
 */
@Data
public class AuthenticationDTO {

    private String role;
    private List<ResourceDTO> resources;

}
