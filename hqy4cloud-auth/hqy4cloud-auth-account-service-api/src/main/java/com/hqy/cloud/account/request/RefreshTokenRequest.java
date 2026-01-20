package com.hqy.cloud.account.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 刷新Token请求参数
 * @author qiyuan.hong
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 客户端ID
     */
    private String clientId;

    /**
     * 客户端密钥
     */
    private String clientSecret;

    /**
     * 刷新令牌
     */
    private String refreshToken;

}
