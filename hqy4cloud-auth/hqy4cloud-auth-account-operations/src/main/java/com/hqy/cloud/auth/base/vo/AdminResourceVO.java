package com.hqy.cloud.auth.base.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/20 13:41
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminResourceVO {

    private Integer id;

    private String name;

    private String path;

    private String method;

    private String permission;

    private String status;

}
