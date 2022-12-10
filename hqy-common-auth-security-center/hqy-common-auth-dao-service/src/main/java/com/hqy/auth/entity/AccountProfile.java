package com.hqy.auth.entity;

import com.hqy.base.BaseEntity;

import javax.persistence.Table;
import java.util.Date;

/**
 * 账户信息表 t_account_profile
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/19 17:03
 */
@Table(name = "t_account_profile")
public class AccountProfile extends BaseEntity<Long> {

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



    public AccountProfile() {

    }

    public AccountProfile(Long id, String nickname, String avatar) {
        super(id, new Date());
        this.nickname = nickname;
        this.avatar = avatar;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }
}
