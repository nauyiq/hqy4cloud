package com.hqy.cloud.auth.base.vo;

import cn.hutool.core.date.DateUtil;
import com.hqy.cloud.account.dto.AccountInfoDTO;
import com.hqy.cloud.foundation.common.account.AvatarHostUtil;
import com.hqy.cloud.common.base.lang.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/12 16:11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfoVO {

    private String id;
    private String username;
    private String nickname;
    private String phone;
    private String email;
    private String birthday;
    private List<String> roles;
    private String avatar;
    private String status;
    private String created;

    public AccountInfoVO(AccountInfoDTO accountInfo) {
        this.id = accountInfo.getId().toString();
        this.username = accountInfo.getUsername();
        this.nickname = accountInfo.getNickname();
        this.phone = accountInfo.getPhone();
        this.email = accountInfo.getEmail();
        this.birthday =  DateUtil.formatDateTime(accountInfo.getBirthday());
        this.avatar = AvatarHostUtil.settingAvatar(accountInfo.getAvatar());
        this.status = accountInfo.getStatus().toString();
        this.created = DateUtil.formatDateTime(accountInfo.getCreated());
        this.roles = Arrays.asList(StringUtils.tokenizeToStringArray(accountInfo.getRoles(), StringConstants.Symbol.COMMA));
    }
}
