package com.hqy.cloud.auth.support.endpoint;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.auth.base.lang.Oauth2ErrorCodesExpand;
import com.hqy.cloud.auth.entity.SysOauthClient;
import com.hqy.cloud.auth.service.tk.SysOauthClientTkService;
import com.hqy.cloud.auth.support.handler.DefaultAuthenticationFailureHandler;
import com.hqy.cloud.auth.utils.Oauth2EndpointUtils;
import com.hqy.cloud.common.base.lang.exception.NotAuthenticationException;
import com.hqy.cloud.common.bind.R;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 令牌端点
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/6 13:38
 */
@RestController
@RequestMapping("/token")
@RequiredArgsConstructor
public class TokenEndpoint {

    private final SysOauthClientTkService sysOauthClientTkService;
    private final OAuth2AuthorizationService oAuth2AuthorizationService;
    private final AuthenticationFailureHandler authenticationFailureHandler = new DefaultAuthenticationFailureHandler();
    private final HttpMessageConverter<OAuth2AccessTokenResponse> accessTokenHttpResponseConverter = new OAuth2AccessTokenResponseHttpMessageConverter();

    /**
     * 认证页面
     * @param modelAndView
     * @param error 表单登录失败处理回调的错误信息
     * @return ModelAndView
     */
    @GetMapping("/login")
    public ModelAndView require(ModelAndView modelAndView, @RequestParam(required = false) String error) {
        modelAndView.setViewName("ftl/login");
        modelAndView.addObject("error", error);
        return modelAndView;
    }


    @GetMapping("/confirm_access")
    public ModelAndView confirm(Principal principal, ModelAndView modelAndView,
                                @RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId,
                                @RequestParam(OAuth2ParameterNames.SCOPE) String scope,
                                @RequestParam(OAuth2ParameterNames.STATE) String state) {

        SysOauthClient clientDetails = sysOauthClientTkService.queryById(clientId);
        if (Objects.isNull(clientDetails)) {
            throw new NotAuthenticationException("clientId 不合法");
        }

        Set<String> authorizedScopes = StringUtils.commaDelimitedListToSet(clientDetails.getScope());
        modelAndView.addObject("clientId", clientId);
        modelAndView.addObject("state", state);
        modelAndView.addObject("scopeList", authorizedScopes);
        modelAndView.addObject("principalName", principal.getName());
        modelAndView.setViewName("ftl/confirm");
        return modelAndView;
    }

    /**
     * 退出登录 删除令牌
     * @param authHeader token.
     * @return           result.
     */
    @DeleteMapping("/logout")
    public R<Boolean> logout(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        if (StrUtil.isBlank(authHeader)) {
            return R.ok();
        }
        String tokenValue = authHeader.replace(OAuth2AccessToken.TokenType.BEARER.getValue(), StrUtil.EMPTY).trim();
        return removeToken(tokenValue);
    }

    /**
     * 删除令牌
     * @param token token.
     * @return      result.
     */
    @DeleteMapping("/{token}")
    public R<Boolean> removeToken(@PathVariable("token") String token) {
        OAuth2Authorization accessToken = oAuth2AuthorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);
        if (Objects.isNull(accessToken)) {
            return null;
        }
        OAuth2Authorization.Token<OAuth2AccessToken> tokenAccessToken = accessToken.getAccessToken();
        if (Objects.isNull(tokenAccessToken) || StrUtil.isBlank(tokenAccessToken.getToken().getTokenValue())) {
            return R.ok();
        }

        oAuth2AuthorizationService.remove(accessToken);
        return R.ok();
    }

    @SneakyThrows
    @GetMapping("/check")
    public void checkToken(String token, HttpServletResponse response, HttpServletRequest request) {
        ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);

        if (StrUtil.isBlank(token)) {
            httpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
            this.authenticationFailureHandler.onAuthenticationFailure(request, response,
                    new InvalidBearerTokenException(Oauth2ErrorCodesExpand.TOKEN_MISSING));
            return;
        }

        OAuth2Authorization authorization = oAuth2AuthorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);
        // 如果令牌不存在 返回401
        if (authorization == null || authorization.getAccessToken() == null) {
            this.authenticationFailureHandler.onAuthenticationFailure(request, response,
                    new InvalidBearerTokenException(Oauth2ErrorCodesExpand.INVALID_BEARER_TOKEN));
            return;
        }

        Map<String, Object> claims = authorization.getAccessToken().getClaims();
        OAuth2AccessTokenResponse sendAccessTokenResponse = Oauth2EndpointUtils.sendAccessTokenResponse(authorization,
                claims);
        this.accessTokenHttpResponseConverter.write(sendAccessTokenResponse, MediaType.APPLICATION_JSON, httpResponse);

    }



}
