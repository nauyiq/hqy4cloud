package com.hqy.order.common.dto;

import com.hqy.order.common.entity.Account;
import com.hqy.order.common.entity.Storage;

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
    public BigDecimal residue;

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



    public OrderDetailDTO(Account account, Storage storage, Integer count, Integer storageResidue) {
        this.residue = account.getResidue();
        this.price = storage.getPrice();
        this.totalMoney = price.multiply(new BigDecimal(count));
        this.storageResidue = storageResidue;
        this.count = count;
    }

    public boolean enableOrder() {
        return !(residue.compareTo(totalMoney) < 0 || storageResidue < count);
    }

    @Override
    public String toString() {
        return "OrderDetailDTO{" +
                "residue=" + residue +
                ", price=" + price +
                ", totalMoney=" + totalMoney +
                '}';
    }
}
