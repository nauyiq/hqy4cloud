package com.hqy.cloud.communication.request;

import com.hqy.cloud.common.request.BaseRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * @author qiyuan.hong
 * @date 2024/7/11
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PhoneMsgParams extends BaseRequest {

    @NotBlank
    private String phone;

    @NotBlank
    private String code;

    @NotBlank
    private String templateId;


}
