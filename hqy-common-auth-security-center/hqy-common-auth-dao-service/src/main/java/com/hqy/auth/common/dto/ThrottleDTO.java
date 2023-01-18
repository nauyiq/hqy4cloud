package com.hqy.auth.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/5 14:51
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThrottleDTO {

    private String ip;
    private Integer type;
    private String throttleBy;
    private Integer blockSeconds;
    private String serviceName;
    private String url;
    private String env;
    private String created;



}
