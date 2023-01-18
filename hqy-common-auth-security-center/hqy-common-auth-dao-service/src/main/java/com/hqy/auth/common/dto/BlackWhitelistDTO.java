package com.hqy.auth.common.dto;

import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.base.common.result.CommonResultCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

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
    private String value;

    /**
     * 过期时间
     */
    private Long expired;


    public BlackWhitelistDTO(String type, String value) {
        this.type = type;
        this.value = value;
    }
}
