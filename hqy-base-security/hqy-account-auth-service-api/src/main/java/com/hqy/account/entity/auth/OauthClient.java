package com.hqy.account.entity.auth;

import com.hqy.base.BaseEntity;
import lombok.Data;

import javax.persistence.Table;

/**
 * @author qiyuan.hong
 * @date 2022-03-16 14:44
 */
@Data
@Table(name = "oauth_client_details")
public class OauthClient extends BaseEntity<String> {

    private String resourceIds;

    private String clientSecret;

    private String scope;

    private String authorizedGrantTypes;

    private String webServerRedirectUri;

    private String authorities;

    private Integer accessTokenValidity;

    private Integer refreshTokenValidity;

    private String additionalInformation;

    private String autoapprove;

}
