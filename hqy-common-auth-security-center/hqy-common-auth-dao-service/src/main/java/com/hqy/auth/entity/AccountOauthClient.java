package com.hqy.auth.entity;

import com.hqy.base.BaseEntity;

import javax.persistence.Table;
import java.util.Date;

/**
 * spring security oauth2
 * @author qiyuan.hong
 * @date 2022-03-16 14:44
 */
@Table(name = "t_account_oauth_client")
public class AccountOauthClient extends BaseEntity<Long> {
    private static final long serialVersionUID = -8627114377849486540L;

    /**
     * 用于唯一标识每一个客户端(client)
     */
    private String clientId;

    /**
     * 客户端所能访问的资源id集合,多个资源时用逗号(,)分隔,如: "unity-resource,mobile-resource".
     * 我们有Resource Server资源服务器。，资源服务器可以有多个，我们可以为每一个Resource Server（一个微服务实例）设置一个resourceid。
     * Authorization Server给client第三方客户端授权的时候，可以设置这个client可以访问哪一些Resource Server资源服务，如果没设置，就是对所有的Resource Server都有访问权限。
     */
    private String resourceIds;

    /**
     * 用于指定客户端(client)的访问密匙
     */
    private String clientSecret;

    /**
     * 指定客户端申请的权限范围,可选值包括read,write,trust
     */
    private String scope = "all";

    /**
     * 指定客户端支持的grant_type,可选值包括authorization_code,password,refresh_token,implicit,client_credentials
     */
    private String authorizedGrantTypes = "password,refresh_token,authorization_code";

    /**
     * 客户端的重定向URI,可为空
     */
    private String webServerRedirectUri;

    /**
     * @PreAuthorize("hasAuthority('admin')")可以在方法上标志 用户或者说client 需要说明样的权限
     * 指定客户端所拥有的Spring Security的权限值
     */
    private String authorities;

    /**
     * 设定客户端的access_token的有效时间值(单位:秒),可选, 若不设定值则使用默认的有效时间值(60 * 60 * 12, 12小时).
     */
    private Integer accessTokenValidity;

    /**
     * 设定客户端的refresh_token的有效时间值(单位:秒),可选, 若不设定值则使用默认的有效时间值(60 * 60 * 24 * 30, 30天).
     */
    private Integer refreshTokenValidity;

    /**
     * 预留的字段,在Oauth的流程中没有实际的使用,可选,但若设置值,必须是JSON格式的数据
     */
    private String additionalInformation;

    /**
     * 设置用户是否自动Approval操作, 默认值为 'false', 可选值包括 'true','false', 'read','write'.
     * 该字段只适用于grant_type="authorization_code"的情况,当用户登录成功后,若该值为'true'或支持的scope值,则会跳过用户Approve的页面, 直接授权.
     */
    private String autoapprove;

    /**
     * 是否可用
     */
    private Boolean status = true;

    public AccountOauthClient() {
    }

    public AccountOauthClient(long id, String clientId) {
        super(new Date());
        super.setId(id);
        this.clientId = clientId;
    }

    public  AccountOauthClient(long id, String clientId, String clientSecret) {
        super(new Date());
        super.setId(id);
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public AccountOauthClient(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(String resourceIds) {
        this.resourceIds = resourceIds;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getAuthorizedGrantTypes() {
        return authorizedGrantTypes;
    }

    public void setAuthorizedGrantTypes(String authorizedGrantTypes) {
        this.authorizedGrantTypes = authorizedGrantTypes;
    }

    public String getWebServerRedirectUri() {
        return webServerRedirectUri;
    }

    public void setWebServerRedirectUri(String webServerRedirectUri) {
        this.webServerRedirectUri = webServerRedirectUri;
    }

    public String getAuthorities() {
        return authorities;
    }

    public void setAuthorities(String authorities) {
        this.authorities = authorities;
    }

    public Integer getAccessTokenValidity() {
        return accessTokenValidity;
    }

    public void setAccessTokenValidity(Integer accessTokenValidity) {
        this.accessTokenValidity = accessTokenValidity;
    }

    public Integer getRefreshTokenValidity() {
        return refreshTokenValidity;
    }

    public void setRefreshTokenValidity(Integer refreshTokenValidity) {
        this.refreshTokenValidity = refreshTokenValidity;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public String getAutoapprove() {
        return autoapprove;
    }

    public void setAutoapprove(String autoapprove) {
        this.autoapprove = autoapprove;
    }
}
