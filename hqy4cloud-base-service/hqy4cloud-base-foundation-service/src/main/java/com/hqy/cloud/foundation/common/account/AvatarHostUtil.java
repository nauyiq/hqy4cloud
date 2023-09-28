package com.hqy.cloud.foundation.common.account;

import com.hqy.cloud.common.base.lang.StringConstants;
import org.apache.commons.lang3.StringUtils;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/10 21:41
 */
public final class AvatarHostUtil {
    public static final String DEFAULT_AVATAR = "/files/avatar/default_avatar.png";

    public static String settingAvatar(String avatar) {
        if (StringUtils.isNotBlank(avatar) && !avatar.startsWith(StringConstants.HTTP)) {
            return StringConstants.Host.HTTPS_FILE_ACCESS + avatar;
        }
        return avatar;
    }

}
