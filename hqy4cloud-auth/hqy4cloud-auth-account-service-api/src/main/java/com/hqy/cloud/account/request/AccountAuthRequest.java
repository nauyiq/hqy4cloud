package com.hqy.cloud.account.request;

import com.hqy.cloud.common.request.BaseRequest;
import lombok.*;

/**
 * @author hongqy
 * @date 2025/2/14
 */
@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountAuthRequest extends BaseRequest {

    private Long id;
    private String rearName;
    private String idCard;

}
