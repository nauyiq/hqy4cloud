package com.hqy.cloud.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/19 9:47
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuConfig {

    private String name;
    private String path;
    private String permission;

}
