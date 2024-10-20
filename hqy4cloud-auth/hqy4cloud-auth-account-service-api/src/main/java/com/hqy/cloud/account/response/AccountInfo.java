package com.hqy.cloud.account.response;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

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
public class AccountInfo implements Serializable {

    /**
     * 用户id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 拥有的权限
     */
    private String role;

    /**
     * 用户拥有的权限
     */
    private String authorities;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 身份证
     */
    private String idCard;

    /**
     * 状态
     */
    private Boolean status;

    /**
     * 是否实名
     */
    private Boolean certification;

    /**
     * 创建时间
     */
    private Date created;



}
