package com.hqy.order.common.entity;

import com.hqy.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 10:32
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "t_account")
public class Account extends BaseEntity<Long> {

    private BigDecimal total;

    private BigDecimal used;

    private BigDecimal residue;


}
