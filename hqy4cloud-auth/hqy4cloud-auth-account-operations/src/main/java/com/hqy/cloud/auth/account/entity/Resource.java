package com.hqy.cloud.auth.account.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hqy.cloud.db.mybatisplus.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * t_resource entity.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/21 17:55
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_resource")
@EqualsAndHashCode(callSuper = true)
public class Resource extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -8196411527093312895L;

    @TableId(type = IdType.AUTO)
    private Integer id;

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
     * 是否删除
     */
    private Boolean deleted = false;

    /**
     * 状态
     */
    private Boolean status;

}
