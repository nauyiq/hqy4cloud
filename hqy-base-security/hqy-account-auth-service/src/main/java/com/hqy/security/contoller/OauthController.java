package com.hqy.security.contoller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.hqy.account.entity.Account;
import com.hqy.account.service.AccountTkService;
import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.bind.MessageResponse;
import com.hqy.base.common.result.CommonResultCode;
import com.hqy.security.dto.OauthAccountRegistryDTO;
import com.hqy.security.server.SentinelOauthExceptionServer;
import com.hqy.security.service.OauthAccountService;
import com.hqy.util.OauthRequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.KeyPair;

/**
 * Oauth 相关接口
 * @author qiyuan.hong
 * @date 2022-03-14 21:33
 */
@Slf4j
@RestController
@RequestMapping("/oauth")
@SessionAttributes("authorizationRequest")
public class OauthController {

    @Resource
    private KeyPair keyPair;

    @Resource
    private OauthAccountService oauthAccountService;

    @Resource
    private AccountTkService accountTkService;

    /**
     * 获取RSA公钥接口
     * @return body for map
     */
    /*@GetMapping("/rsa/publicKey")
    public Map<String, Object> getPublicKey() {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAKey key = new RSAKey.Builder(publicKey).build();
        return new JWKSet(key).toJSONObject();
    }*/


    @PostMapping("/registry")
    public MessageResponse registry(@RequestBody @Valid OauthAccountRegistryDTO account) {
        return oauthAccountService.registry(account);
    }


    @GetMapping("/me")
    @SentinelResource(value = "oauth-me",
            blockHandler = "blockHandler", blockHandlerClass = SentinelOauthExceptionServer.Oauth2MeInterfaceHandler.class,
            fallback = "fallbackHandler", fallbackClass = SentinelOauthExceptionServer.Oauth2MeInterfaceHandler.class)
    public DataResponse currentUser(HttpServletRequest request) {
        Long id = OauthRequestUtil.idFromOauth2Request(request);
        Account account = accountTkService.queryById(id);
        return CommonResultCode.dataResponse(true, CommonResultCode.SUCCESS, account);
    }

    @GetMapping("/callback")
    public DataResponse oauth2Callback(String code) {
        return CommonResultCode.dataResponse(code);
    }



    /*@PostMapping("/token")
    public Object postAccessToken(
           Principal principal,
           @RequestParam Map<String, String> parameters
    ) throws HttpRequestMethodNotSupportedException {

        *//**
         * 获取登录认证的客户端ID
         *
         * 兼容两种方式获取Oauth2客户端信息（client_id、client_secret）
         * 方式一：client_id、client_secret放在请求路径中(注：当前版本已废弃)
         * 方式二：放在请求头（Request Headers）中的Authorization字段，且经过加密，例如 Basic Y2xpZW50OnNlY3JldA== 明文等于 client:secret
         *//*
        String clientId = RequestUtils.getOAuth2ClientId();
        log.info("OAuth认证授权 客户端ID:{}，请求参数：{}", clientId, JSONUtil.toJsonStr(parameters));

        *//**
         * knife4j接口文档测试使用
         *
         * 请求头自动填充，token必须原生返回，不能有任何包装，否则显示 undefined undefined
         * 账号/密码:  client_id/client_secret : client/123456
         *//*
        if (SecurityConstants.TEST_CLIENT_ID.equals(clientId)) {
            return tokenEndpoint.postAccessToken(principal, parameters).getBody();
        }

        OAuth2AccessToken accessToken = tokenEndpoint.postAccessToken(principal, parameters).getBody();
        return Result.success(accessToken);
    }*/




}
