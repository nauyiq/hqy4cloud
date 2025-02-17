package com.hqy.cloud.account.response;

import lombok.*;

import java.io.Serializable;

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
public class AccountOperationInfo implements Serializable {

    private AccountInfo accountInfo;

    public static AccountOperationInfo of(AccountInfo accountInfo) {
        return AccountOperationInfo.builder().accountInfo(accountInfo).build();
    }

}
