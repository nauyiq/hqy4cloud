package com.hqy.common.entity.account;

import com.hqy.base.BaseEntity;

import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 钱包表
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/8 10:32
 */
@Table(name = "t_wallet")
public class Wallet extends BaseEntity<Long> {

    private BigDecimal money;

    public Wallet() {
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }
}
