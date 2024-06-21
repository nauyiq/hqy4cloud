package com.hqy.cloud.limit.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/16 11:05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlockDTO implements Serializable {

    private Long blockedMillis;
    private Long blockedTimestamp;

}
