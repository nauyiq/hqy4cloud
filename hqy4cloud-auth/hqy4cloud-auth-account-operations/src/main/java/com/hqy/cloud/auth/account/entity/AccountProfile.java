package com.hqy.cloud.auth.account.entity;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hqy.cloud.auth.base.AccountConstants;
import com.hqy.cloud.db.mybatisplus.BaseEntity;
import com.hqy.cloud.file.domain.AccountAvatarUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

import static com.hqy.cloud.file.domain.AccountAvatarUtil.DEFAULT_AVATAR;


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

    public static AccountProfile register(long accountId, String nickname, String username, String phone, String avatar) {
        if (StringUtils.isBlank(nickname)) {
            // 随机生成昵称， 如果是手机注册的话采用 前缀 + 6位随机数 + 手机号后四位， 手机为空的话 则后四位采用username拼接，username不足四位则username全部拼接
            if (StringUtils.isBlank(phone)) {
                nickname = AccountConstants.DEFAULT_NICKNAME_PREFIX + RandomUtil.randomString(6) + (username.length() > 4 ? username.substring(0, 4) : username);
            } else {
                nickname = AccountConstants.DEFAULT_NICKNAME_PREFIX + RandomUtil.randomString(6) + phone.substring(7, 11);
            }
        }
        avatar = StringUtils.isBlank(avatar) ? DEFAULT_AVATAR : AccountAvatarUtil.extractAvatar(avatar);
        return new AccountProfile(accountId, nickname, avatar);
    }
}
