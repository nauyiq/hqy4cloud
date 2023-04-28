package com.hqy.cloud.web.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/9 10:22
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AccountPayloadDTO {

    /**
     * id
     */
    public Long id;

    /**
     * 密码
     */
    public String password;

    /**
     * 邮箱
     */
    public String email;

    /**
     * 用户名
     */
    public String username;




}
