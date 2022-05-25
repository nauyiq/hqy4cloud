package com.hqy.common.entity.order;

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
@Table(name = "t_order")
public class Order extends BaseEntity<Long> {

    private Long accountId;

    private Long productId;

    private Integer count;

    private BigDecimal money;

    private Boolean status;

    public Order() {
    }

    public Order(Long accountId, Long productId, Integer count, BigDecimal money, Boolean status, Date date) {
        super(date);
        this.accountId = accountId;
        this.productId = productId;
        this.count = count;
        this.money = money;
        this.status = status;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
