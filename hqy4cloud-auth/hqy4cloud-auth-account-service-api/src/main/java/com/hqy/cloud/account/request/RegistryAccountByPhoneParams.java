package com.hqy.cloud.account.request;

import com.hqy.cloud.common.request.BaseRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 注册账号参数
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/16
 */
@Getter
@Setter
@ToString
public class RegistryAccountByPhoneParams extends BaseRequest {

    /**
     * 用户名为空时随机生成用户名
     */
    private String username;

    /**
     * 客户端ID
     */
    private String clientId;

    /**
     * 手机号码
     */
    @NotBlank
    private String phone;

    /**
     * 验证码
     */
    @NotBlank
    private String code;

    /**
     * 没有密码的时随机生成密码
     */
    private String password;

    /**
     * 头像
     */
    private String avatar;

}
