package com.hqy.auth.contoller;

import com.hqy.auth.dto.OauthAccountDTO;
import com.hqy.auth.service.OauthAccountService;
import com.hqy.base.common.bind.MessageResponse;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

/**
 * Oauth 相关接口
 * @author qiyuan.hong
 * @date 2022-03-14 21:33
 */
@RestController
public class OauthController {

    @Resource
    private KeyPair keyPair;

    @Resource
    private OauthAccountService oauthAccountService;

    /**
     * 获取RSA公钥接口
     * @return body for map
     */
    @GetMapping("/rsa/publicKey")
    public Map<String, Object> getPublicKey() {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAKey key = new RSAKey.Builder(publicKey).build();
        return new JWKSet(key).toJSONObject();
    }


    @PostMapping("/oauth/registry")
    public MessageResponse registry(@RequestBody @Valid OauthAccountDTO account) {
        return oauthAccountService.registry(account);
    }




}
