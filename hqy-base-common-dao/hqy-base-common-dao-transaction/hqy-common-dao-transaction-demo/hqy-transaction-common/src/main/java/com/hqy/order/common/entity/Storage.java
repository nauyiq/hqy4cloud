package com.hqy.order.common.entity;

import com.hqy.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Table;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 10:32
 */
@Data
@Table(name = "t_storage")
@EqualsAndHashCode(callSuper = true)
public class Storage extends BaseEntity<Long> {


    private Long productId;

    private Integer total;

    private Integer used;

    private Integer residue;

}
