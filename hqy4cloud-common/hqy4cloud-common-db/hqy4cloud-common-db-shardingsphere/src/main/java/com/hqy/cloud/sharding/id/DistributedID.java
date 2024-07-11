package com.hqy.cloud.sharding.id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * 分布式ID， 基因法生成ID
 * 10（业务码） 1769649671860822016（sequence) 1023(分表）
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/8
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DistributedID {

    /**
     * 业务系统标识码
     */
    private String businessCode;

    /**
     * 表下标
     */
    private String tableIndex;

    /**
     * 序列号
     */
    private String seq;


    @Override
    public String toString() {
        return this.businessCode + this.seq + this.tableIndex;
    }

    public long getId() {
        return Long.parseLong(this.toString());
    }


    public static DistributedID create(String businessCode, Long seq, String tableIndex) {
        DistributedID distributedID = new DistributedID();
        distributedID.businessCode = businessCode;
        // 自动补齐左侧字符
        if (StringUtils.isNotBlank(tableIndex)) {
            distributedID.tableIndex = StringUtils.leftPad(tableIndex, 4, "0");
        }
        distributedID.seq = String.valueOf(seq);
        return distributedID;
    }

    public static DistributedID valueOf(String id) {
        DistributedID distributeId = new DistributedID();
        distributeId.businessCode = id.substring(0, 1);
        distributeId.seq = id.substring(1, id.length() - 4);
        distributeId.tableIndex = id.substring(id.length() - 4);
        return distributeId;
    }






}
