package com.hqy.cloud.auth.base.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/8 16:01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDTO {
    private String role;
    private List<String> permissions;

}
