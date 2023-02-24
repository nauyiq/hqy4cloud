package com.hqy.cloud.common.vo.menu;

import lombok.Data;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/12 10:48
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
    private String visible;


}
