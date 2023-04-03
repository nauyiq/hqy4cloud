package com.hqy.cloud.auth.utils;

import com.hqy.account.dto.AccountInfoDTO;
import com.hqy.cloud.common.base.lang.StringConstants;
import org.apache.commons.lang3.StringUtils;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/10 21:41
 */
public final class AvatarHostUtil {

    public static void settingAvatar(AccountInfoDTO accountInfoDTO) {
        String avatar = accountInfoDTO.getAvatar();
        avatar = settingAvatar(avatar);
        accountInfoDTO.setAvatar(avatar);
    }

    public static String settingAvatar(String avatar) {
        if (StringUtils.isNotBlank(avatar) && !avatar.startsWith(StringConstants.HTTP)) {
            return StringConstants.Host.HTTPS_FILE_ACCESS + avatar;
        }
        return avatar;
    }

}
