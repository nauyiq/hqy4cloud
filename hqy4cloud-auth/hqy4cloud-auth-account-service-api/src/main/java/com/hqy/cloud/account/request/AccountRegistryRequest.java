package com.hqy.cloud.account.request;

import com.hqy.cloud.account.constants.GrantType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * @author hongqy
 * @date 2026/1/14
 */
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AccountRegistryRequest {

    /**
     * 用户名为空时随机生成用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 验证码
     */
    private String code;


    /**
     * 客户端ID
     */
    @NotNull(message = "客户端ID不能为空")
    private String clientId;

    /**
     * 注册方式
     */
    @NotNull(message = "注册方式不能为空")
    private GrantType registerType;


    public static AccountRegistryRequest of(AuthenticateRequest authenticateRequest) {
        AccountRegistryRequest request = new AccountRegistryRequest();
        request.setCode(authenticateRequest.getAccessSecret());
        request.setClientId(authenticateRequest.getClientId());
        GrantType grantType = authenticateRequest.getGrantType();
        request.setRegisterType(grantType);
        if (grantType == GrantType.SMS) {
            request.setPhone(authenticateRequest.getAccessAccount());
        } else {
            request.setEmail(authenticateRequest.getAccessAccount());
        }
        return request;
    }

}
