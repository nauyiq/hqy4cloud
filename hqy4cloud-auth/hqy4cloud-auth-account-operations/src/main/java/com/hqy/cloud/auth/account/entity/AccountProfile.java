package com.hqy.cloud.auth.account.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hqy.cloud.db.mybatisplus.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 账户信息表 t_account_profile
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_account_profile")
public class AccountProfile extends BaseEntity {


    /**
     * id
     */
    @TableId
    private Long id;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 性别
     */
    private Integer sex;

    /**
     * 简介
     */
    private String intro;

    /**
     * 生日
     */
    private Date birthday;

    public AccountProfile() {

    }

    public AccountProfile(Long id, String nickname, String avatar) {
        super(new Date());
        this.id = id;
        this.nickname = nickname;
        this.avatar = avatar;
    }


}
