package com.hqy.cloud.account.response;

import lombok.*;

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
public class AccountInfo {

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
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 简介
     */
    private String intro;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 性别
     */
    private Integer sex;

    /**
     * 状态
     */
    private Boolean status;

    /**
     * 创建时间
     */
    private Date created;



}
