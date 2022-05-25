package com.hqy.common.dto;

import com.hqy.common.entity.account.Wallet;
import com.hqy.common.entity.storage.Storage;

import java.math.BigDecimal;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/20 11:28
 */
public class OrderDetailDTO {

    /**
     * 账户可用余额
     */
    public BigDecimal money;

    /**
     * 库存
     */
    public Integer storageResidue;

    /**
     * 商品价格
     */
    public BigDecimal price;

    /**
     * 购买数目
     */
    public Integer count;

    /**
     * 总价
     */
    public BigDecimal totalMoney;



    public OrderDetailDTO(Wallet wallet, Storage storage, Integer count, Integer storageResidue) {
        this.money = wallet.getMoney();
        this.price = storage.getPrice();
        this.totalMoney = price.multiply(new BigDecimal(count));
        this.storageResidue = storageResidue;
        this.count = count;
    }

    public boolean enableOrder() {
        return !(money.compareTo(totalMoney) < 0 || storageResidue < count);
    }

    @Override
    public String toString() {
        return "OrderDetailDTO{" +
                "residue=" + money +
                ", price=" + price +
                ", totalMoney=" + totalMoney +
                '}';
    }
}
