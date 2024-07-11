package com.hqy.cloud.infrastructure.random;

import java.util.List;

/**
 * 随机生成验证码的场景
 * @author qiyuan.hong
 * @date 2024/7/11
 */
public enum RandomCodeScene {

    /**
     * 手机验证码认证
     */
    SMS_AUTH(List.of("code", "sms").toArray(new String[0])),

    /**
     * 邮箱验证码认证
     */
    EMAIL_AUTH(List.of("code", "email").toArray(new String[0])),


    ;



    public final String[] PARAMS;


    RandomCodeScene(String[] PARAMS) {
        this.PARAMS = PARAMS;
    }

}
