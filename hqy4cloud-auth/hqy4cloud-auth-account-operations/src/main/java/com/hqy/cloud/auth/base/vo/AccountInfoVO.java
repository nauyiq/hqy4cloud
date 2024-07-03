package com.hqy.cloud.auth.base.vo;

import cn.hutool.core.date.DateUtil;
import com.hqy.cloud.auth.base.dto.AccountInfoDTO;
import com.hqy.cloud.auth.common.UserRole;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.file.domain.AccountAvatarUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/12
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
    private UserRole userRole;
    private List<String> authorities;
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
        this.avatar = AccountAvatarUtil.getAvatar(accountInfo.getAvatar());
        this.status = accountInfo.getStatus().toString();
        this.created = DateUtil.formatDateTime(accountInfo.getCreated());
        this.userRole = accountInfo.getRole();
        this.authorities = Arrays.asList(StringUtils.tokenizeToStringArray(accountInfo.getAuthorities(), StringConstants.Symbol.COMMA));
    }
}
