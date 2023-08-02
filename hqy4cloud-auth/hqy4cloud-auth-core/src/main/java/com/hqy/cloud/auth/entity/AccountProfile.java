package com.hqy.cloud.auth.entity;

import com.hqy.cloud.db.tk.model.BaseEntity;
import lombok.Data;

import javax.persistence.Table;
import java.util.Date;

/**
 * 账户信息表 t_account_profile
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/19 17:03
 */
@Data
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

    /**
     * chaptgpt配置
     */
    private String chatgptConfig;



    public AccountProfile() {

    }

    public AccountProfile(Long id, String nickname, String avatar) {
        super(id, new Date());
        this.nickname = nickname;
        this.avatar = avatar;
    }


}
