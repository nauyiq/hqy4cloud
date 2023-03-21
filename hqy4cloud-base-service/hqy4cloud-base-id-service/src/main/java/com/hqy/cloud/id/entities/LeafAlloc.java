package com.hqy.cloud.id.entities;

import com.hqy.cloud.tk.PrimaryLessBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/21 15:02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_leaf_alloc")
public class LeafAlloc implements PrimaryLessBaseEntity {

    /**
     * 区分业务,业务标志id
     */
    @Id
    private String bizTag;

    /**
     *  该biz_tag目前所被分配的ID号段的最大值
     */
    private Long maxId;

    /**
     *  每次分配的号段长度
     */
    private int step;

    /**
     *  每次getid时随机增加的长度，这样就不会有连续的id了， 默认1
     */
    private int randomStep;

    /**
     *  描述
     */
    private String description;

    /**
     * 更新时间
     */
    private Long updated;

    public void setKey(String key) {
        this.bizTag = key;
    }





}
