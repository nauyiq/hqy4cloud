package com.hqy.foundation.id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnowFlakeDTO implements Serializable{

    /**
     * 注册时间
     */
    private Long timestamp;

    /**
     * 注册worker id
     */
    private Integer workerId;



}
