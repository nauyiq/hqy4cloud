package com.hqy.common.entity.account;

import com.hqy.base.BaseEntity;

import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 订单钱包表
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/23 14:44
 */
@Table(name = "t_order_wallet")
public class OrderWallet extends BaseEntity<Long> {

    private Long orderId;

    private BigDecimal money;

    private Boolean status;

    public OrderWallet() {
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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
