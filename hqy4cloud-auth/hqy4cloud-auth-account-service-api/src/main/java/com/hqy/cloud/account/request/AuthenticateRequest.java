package com.hqy.cloud.account.request;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.annotation.JSONField;
import com.hqy.cloud.account.constants.GrantType;
import com.hqy.cloud.common.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;

/**
 * RPC 认证请求参数
 * @author qiyuan.hong
 */
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticateRequest extends BaseRequest {

    /**
     * 客户端ID
     */
    @NotNull(message = "客户端ID不能为空")
    private String clientId;

    /**
     * 客户端密钥
     */
    @NotNull(message = "商户密钥不能为空")
    private String clientSecret;

    /**
     * 用户名/手机号/邮箱
     */
    @NotNull(message = "访问账号不能为空")
    private String accessAccount;

    /**
     * 访问密钥（password模式为密码， sms/email模式为对应的验证码）
     */
    @NotNull(message = "授权密钥不能为空")
    private String accessSecret;

    /**
     * 授权类型
     */
    @NotNull(message = "授权类型不能为空")
    private GrantType grantType;

    /**
     * 授权范围
     */
    private Set<String> scopes;


    @JSONField(serialize = false)
    public String getPassword() {
        return this.grantType == GrantType.PASSWORD ? this.accessSecret : StrUtil.EMPTY;
    }


    @JSONField(serialize = false)
    public String getEmail() {
        return this.grantType == GrantType.EMAIL ? this.accessAccount : StrUtil.EMPTY;
    }

    @JSONField(serialize = false)
    public String getPhone() {
        return this.grantType == GrantType.SMS ? this.accessAccount : StrUtil.EMPTY;
    }


}
