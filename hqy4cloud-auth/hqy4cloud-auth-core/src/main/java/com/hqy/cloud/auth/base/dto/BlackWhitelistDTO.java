package com.hqy.cloud.auth.base.dto;

import com.hqy.cloud.common.base.lang.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/10 16:24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlackWhitelistDTO {

    /**
     * 类型
     */
    @NotEmpty(message = StringConstants.SHOULD_NOT_BE_EMPTY)
    private String type;

    /**
     * 值
     */
    @NotEmpty(message = StringConstants.SHOULD_NOT_BE_EMPTY)
    private String ip;

    /**
     * 过期时间
     */
    private Long expired;


    public BlackWhitelistDTO(String type, String ip) {
        this.type = type;
        this.ip = ip;
    }
}
