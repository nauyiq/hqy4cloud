package com.hqy.cloud.account.request;

import com.hqy.cloud.common.request.BaseRequest;
import lombok.*;

/**
 * @author hongqy
 * @date 2025/3/31
 */
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AccountModifyRequest extends BaseRequest {

    private Long id;

    private String oldPassword;

    private String newPassword;


}
