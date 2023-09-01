package com.hqy.cloud.auth.base.vo;

import cn.hutool.core.date.DateUtil;
import com.hqy.cloud.account.dto.AccountInfoDTO;
import com.hqy.cloud.auth.utils.AvatarHostUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * AdminUserInfoVO.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/10 21:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserInfoVO {

    private List<String> permissions;

    private List<String> roles;

    private SysUser sysUser;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SysUser {
        private String id;
        private String username;
        private String nickname;
        private String avatar;
        private String email;
        private String phone;
        private String intro;
        private String birthday;
        private String created;

        public SysUser(AccountInfoDTO accountInfo) {
            this.id = accountInfo.getId().toString();
            this.username = accountInfo.getUsername();
            this.nickname = accountInfo.getNickname();
            this.avatar = AvatarHostUtil.settingAvatar(accountInfo.getAvatar());
            this.email = accountInfo.getEmail();
            this.phone = accountInfo.getPhone();
            this.intro = accountInfo.getIntro();
            this.birthday = DateUtil.formatDateTime(accountInfo.getBirthday());
            this.created = DateUtil.formatDateTime(accountInfo.getCreated());
        }
    }


}
