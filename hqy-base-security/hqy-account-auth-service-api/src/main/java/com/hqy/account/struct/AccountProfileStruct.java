package com.hqy.account.struct;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;

/**
 * AccountProfileStruct.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/29 18:08
 */
@ThriftStruct
public final class AccountProfileStruct {

    @ThriftField(1)
    public Long id;
    @ThriftField(2)
    public String nickname;
    @ThriftField(3)
    public String avatar;
    @ThriftField(4)
    public String intro;
    @ThriftField(5)
    public String birthday;

    public AccountProfileStruct() {
    }

    public AccountProfileStruct(Long id, String nickname, String avatar, String intro, String birthday) {
        this.id = id;
        this.nickname = nickname;
        this.avatar = avatar;
        this.intro = intro;
        this.birthday = birthday;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
