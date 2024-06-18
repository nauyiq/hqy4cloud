package com.hqy.cloud.infrastructure.random;

/**
 * 随机生成验证码的场景
 * @author qiyuan.hong
 * @date 2024/7/11
 */
public enum RandomCodeScene {

    /**
     * 手机验证码认证
     */
    SMS_AUTH("code:sms:"),

    /**
     * 邮箱验证码认证
     */
    EMAIL_AUTH("code:email:"),


    ;



    public final String suffix;


    RandomCodeScene(String suffix) {
        this.suffix = suffix;
    }

}
