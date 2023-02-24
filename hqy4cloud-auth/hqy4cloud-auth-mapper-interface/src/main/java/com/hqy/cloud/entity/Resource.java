package com.hqy.cloud.entity;

import com.hqy.cloud.tk.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Table;

/**
 *
 * t_resource entity.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/21 17:55
 */
@Data
@Table(name = "t_resource")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Resource extends BaseEntity<Integer> {
    private static final long serialVersionUID = -8196411527093312895L;

    /**
     * 资源名
     */
    private String name;

    /**
     * 资源路径
     */
    private String path;

    /**
     * http method?
     */
    private String method;

    /**
     * 权限标识
     */
    private String permission;

    /**
     * 是否删除
     */
    private Boolean deleted = false;


    /**
     * 状态
     */
    private Boolean status;

}
