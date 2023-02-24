package com.hqy.access.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 角色认证数据
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/19 9:55
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleAuthenticationDTO {

    /**
     * 角色名
     */
    private String role;

    /**
     * 资源配置
     */
    private List<ResourceConfig> resourceConfigs;


}
