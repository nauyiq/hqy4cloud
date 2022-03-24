package com.hqy.auth.encoder;

import com.hqy.base.common.swticher.HttpGeneralSwitcher;
import com.hqy.util.Md5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 使用md5加密的密码编码器 要求前端自己加密. 对于密码授权模式使用规则.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/11 17:48
 */
@Component
public class DefaultPasswordEncoder implements PasswordEncoder {

    private static final Logger log = LoggerFactory.getLogger(DefaultPasswordEncoder.class);

    @Override
    public String encode(CharSequence rawPassword) {
        if (HttpGeneralSwitcher.ENABLE_ACCOUNT_PASSWORD_ENCODER_ENCODE.isOff()) {
            log.info("@@@ PasswordEncoder encoder ignore.");
            return "";
        }
        return Md5Util.getStrMd5(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return rawPassword.equals(encodedPassword);
    }
}
