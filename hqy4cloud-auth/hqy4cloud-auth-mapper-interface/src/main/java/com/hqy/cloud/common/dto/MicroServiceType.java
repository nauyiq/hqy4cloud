package com.hqy.cloud.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/6 17:12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MicroServiceType {

    private String name;
    private String label;
    private String value;

}
