package com.hqy.account.dto;

import com.hqy.account.struct.AccountBaseInfoStruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/8 11:09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AccountBaseInfoDTO {

    private Long id;
    private String nickname;
    private String username;
    private String email;

    public AccountBaseInfoDTO(AccountBaseInfoStruct struct) {
        this.id = struct.id;
        this.nickname = struct.nickname;
        this.username = struct.username;
        this.email = struct.email;
    }
}
