package com.hqy.cloud.auth.base.dto;

import com.github.houbb.sensitive.annotation.strategy.SensitiveStrategyEmail;
import com.github.houbb.sensitive.annotation.strategy.SensitiveStrategyPhone;
import com.hqy.cloud.auth.common.UserRole;
import lombok.*;

import java.util.Date;

/**
 * 用户信息DTO
 * account表和account_profile表的宽表实体
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/27
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountInfoDTO  {

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
    @SensitiveStrategyPhone
    private String phone;

    /**
     * 邮箱
     */
    @SensitiveStrategyEmail
    private String email;

    /**
     * 拥有的权限
     */
    private UserRole role;

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
