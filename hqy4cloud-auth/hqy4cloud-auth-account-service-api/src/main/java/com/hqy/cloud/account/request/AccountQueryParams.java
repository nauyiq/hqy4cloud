package com.hqy.cloud.account.request;

import lombok.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/11
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AccountQueryParams {

    private Long id;
    private String phone;
    private String email;

}
