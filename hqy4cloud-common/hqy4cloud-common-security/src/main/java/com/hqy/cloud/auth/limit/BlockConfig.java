package com.hqy.cloud.auth.limit;

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
public class BlockConfig implements Serializable {

    private Integer frequency;
    private Long blockedMillis;

    public void increment() {
        this.frequency = this.frequency + 1;
    }

}
