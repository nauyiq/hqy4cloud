package com.hqy.common.entity;

import com.hqy.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 10:31
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "t_order")
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BaseEntity<Long> {

    private Long accountId;

    private Long productId;

    private Integer count;

    private BigDecimal money;

    private Boolean status;

    public Order(Long accountId, Long productId, Integer count, BigDecimal money, Boolean status, Date date) {
        super(date);
        this.accountId = accountId;
        this.productId = productId;
        this.count = count;
        this.money = money;
        this.status = status;
    }
}
