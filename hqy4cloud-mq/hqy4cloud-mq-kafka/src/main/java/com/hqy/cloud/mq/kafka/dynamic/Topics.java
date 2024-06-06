package com.hqy.cloud.mq.kafka.dynamic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/6 16:22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Topics {

    /**
     * 主题
     */
    private String name;

    /**
     * 分区数
     */
    private Integer partition;


    /**
     * 分区副本数
     */
    private Integer replications;





}
