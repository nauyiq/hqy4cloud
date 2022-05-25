package com.hqy.common.entity.storage;

import com.hqy.base.BaseEntity;

import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * @author qiyuan.hong
 * @date 2022-05-25 23:34
 */
@Table(name = "t_sec_kill_product")
public class SecKillProduct extends BaseEntity<Long> {

    private Long productId;

    private BigDecimal killPrice;

    private Integer stock;

    private Boolean status = true;

    public SecKillProduct() {
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public BigDecimal getKillPrice() {
        return killPrice;
    }

    public void setKillPrice(BigDecimal killPrice) {
        this.killPrice = killPrice;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
