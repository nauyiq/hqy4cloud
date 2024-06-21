package com.hqy.cloud.auth.base.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/11/14 15:20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlackAddressVO {

    private String type;
    private String ip;
    private Long blockSeconds;
    private String expireDateTime;

}
