package com.hqy.order.common.entity;

import com.hqy.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 10:31
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "t_order")
public class Order extends BaseEntity<Long> {

    private Long accountId;

    private Long productId;

    private Integer count;

    private BigDecimal money;

    private Boolean status;


}
