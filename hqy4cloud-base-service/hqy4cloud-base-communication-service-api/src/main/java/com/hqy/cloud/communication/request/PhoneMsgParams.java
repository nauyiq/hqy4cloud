package com.hqy.cloud.communication.request;

import com.hqy.cloud.common.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * @author qiyuan.hong
 * @date 2024/7/11
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhoneMsgParams extends BaseRequest {

    @NotNull(message = "客户端ID不能为空")
    private String clientId;

    @NotNull(message = "手机号码不能为空")
    private String phone;

    @NotNull(message = "短信类型不能为空")
    private SmsType smsType;

    /**
     * 短信有效期, 不传默认为10分钟有效
     */
    private Integer expiredSeconds;

    public PhoneMsgParams(String clientId, String phone, SmsType smsType) {
        this.clientId = clientId;
        this.phone = phone;
        this.smsType = smsType;
    }
}
