package com.hqy.cloud.auth.base.vo;

import lombok.Data;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/12
 */
@Data
public class BaseMenuVO {

    private Long id;
    private Integer parentId;
    private Integer menuType;
    private String icon;
    private String name;
    private String permission;
    private String label;
    private String path;
    private Integer sortOrder;
    private String keepAlive = "0";
    private Integer status;
    private String visible = "1";


}
