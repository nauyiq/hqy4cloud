package com.hqy.account.dto;

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
    private String avatar;
    private String roles;

}
