package com.hqy.auth.common.vo.menu;

import lombok.Data;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/12 10:48
 */
@Data
public class BaseMenuVO {

    private Integer id;
    private Integer parentId;
    private Integer menuType;
    private String icon;
    private String name;
    private String permission;
    private String label;
    private String path;
    private Integer sortOrder;
    private String keepAlive;
    private Integer status;
    private String visible;


}
