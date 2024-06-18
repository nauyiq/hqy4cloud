package com.hqy.cloud.account.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * 注册账号
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/16
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterInfo implements Serializable {

    private Long id;
    private String username;
    private String nickname;
    private String avatar;


}
