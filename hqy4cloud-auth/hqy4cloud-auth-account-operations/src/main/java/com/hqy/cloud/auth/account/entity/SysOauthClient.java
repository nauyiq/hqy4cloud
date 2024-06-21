package com.hqy.cloud.auth.account.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hqy.cloud.db.mybatisplus.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.util.Date;

/**
 * spring security oauth2
 * @author qiyuan.hong
 * @date 2022-03-16 14:44
 */
@Getter
@Setter
@TableName("t_sys_oauth_client")
public class SysOauthClient extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -8627114377849486540L;

    /**
     * 用于唯一标识每一个客户端(client)
     */
    @TableId
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


    public SysOauthClient() {
    }

    public SysOauthClient(String clientId) {
        super(new Date());
        this.clientId = clientId;
    }

    public SysOauthClient(String clientId, String clientSecret) {
        super(new Date());
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }


}
