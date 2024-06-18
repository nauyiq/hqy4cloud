package com.hqy.cloud.common.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 已认证用户信息.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/6 9:55
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationInfo {

    private Long id;
    private String name;
    private String email;
    private List<String> roles;



}
