package com.hqy.cloud.account.response;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

/**
 * Token响应信息
 * @author qiyuan.hong
 */
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TokenInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 令牌类型
     */
    private String tokenType;

    /**
     * 过期时间（秒）
     */
    private Long expiresIn;

    /**
     * 授权范围
     */
    private Set<String> scopes;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

}
