package com.hqy.cloud.auth.support.core;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.hqy.cloud.auth.common.SecurityConstants;
import com.hqy.cloud.auth.security.api.UserDetailsServiceWrapper;
import com.hqy.cloud.auth.security.common.Oauth2EndpointUtils;
import com.hqy.cloud.auth.security.core.Oauth2ErrorCodesExpand;
import com.hqy.cloud.auth.utils.WebUtils;
import com.hqy.cloud.infrastructure.random.RandomCodeScene;
import com.hqy.cloud.infrastructure.random.RandomCodeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.www.BasicAuthenticationConverter;
import org.springframework.util.Assert;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 数据层面的认证提供者
 *
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/24
 */
public class DefaultDaoAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
    private PasswordEncoder passwordEncoder;
    private static final String USER_NOT_FOUND_PASSWORD = "userNotFoundPassword";
    private final static BasicAuthenticationConverter basicConvert = new BasicAuthenticationConverter();


    /**
     * The password used to perform {@link PasswordEncoder#matches(CharSequence, String)}
     * on when the user is not found to avoid SEC-2056. This is necessary, because some
     * {@link PasswordEncoder} implementations will short circuit if the password is not
     * in a valid format.
     */
    private volatile String userNotFoundEncodedPassword;


    public DefaultDaoAuthenticationProvider(MessageSource messageSource) {
        setMessageSource(messageSource);
//        setPasswordEncoder(new BCryptPasswordEncoder());
        // FIXME 不隐藏用户找不到异常
//        setHideUserNotFoundExceptions(false);
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        Optional<HttpServletRequest> optional = WebUtils.getRequest();
        if (optional.isPresent()) {
            HttpServletRequest request = optional.get();
            String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
            String code = request.getParameter(SecurityConstants.CODE_PARAMETER_NAME);

            if (grantType.equals(SecurityConstants.SMS)) {
                // 校验手机验证是否正确
                RandomCodeService service = SpringUtil.getBean(RandomCodeService.class);
                String phone = request.getParameter(SecurityConstants.PHONE_PARAMETER_NAME);
                if (StringUtils.isBlank(phone) || !service.isExist(code, phone, RandomCodeScene.SMS_AUTH)) {
                    Oauth2EndpointUtils.throwError(Oauth2ErrorCodesExpand.INVALID_REQUEST_CODE, Oauth2ErrorCodesExpand.INVALID_REQUEST_CODE,
                            Oauth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI);
                }
                return;
            }

            if (grantType.equals(SecurityConstants.EMAIL)) {
                // 校验邮箱验证是否正确
                RandomCodeService service = SpringUtil.getBean(RandomCodeService.class);
                String email = request.getParameter(SecurityConstants.EMAIL_PARAMETER_NAME);
                if (StringUtils.isBlank(email) || !service.isExist(code, email, RandomCodeScene.EMAIL_AUTH)) {
                    Oauth2EndpointUtils.throwError(Oauth2ErrorCodesExpand.INVALID_REQUEST_CODE, Oauth2ErrorCodesExpand.INVALID_REQUEST_CODE,
                            Oauth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI);
                }
                return;
            }

            //验证密码
            if (authentication.getCredentials() == null) {
                this.logger.debug("Failed to authenticate since no credentials provided");
                throw new BadCredentialsException(this.messages
                        .getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
            }
            String presentedPassword = authentication.getCredentials().toString();
            if (!this.passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
                this.logger.debug("Failed to authenticate since password does not match stored value");
                throw new BadCredentialsException(this.messages
                        .getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
            }
        }
    }

    @Override
    @SneakyThrows
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        prepareTimingAttackProtection();
        HttpServletRequest request = WebUtils.getRequest().orElseThrow(
                (Supplier<Throwable>) () -> new InternalAuthenticationServiceException("web request is empty"));
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        String clientId = request.getParameter(OAuth2ParameterNames.CLIENT_ID);
        if (StrUtil.isBlank(clientId)) {
            clientId = basicConvert.convert(request).getName();
        }

        Map<String, UserDetailsServiceWrapper> userDetailsServiceMap = SpringUtil.getBeansOfType(UserDetailsServiceWrapper.class);

        String finalClientId = clientId;
        Optional<UserDetailsServiceWrapper> optional = userDetailsServiceMap.values().stream()
                .filter(service -> service.support(finalClientId, grantType))
                .max(Comparator.comparingInt(Ordered::getOrder));

        if (optional.isEmpty()) {
            throw new InternalAuthenticationServiceException("UserDetailsService error, not register.");
        }

        try {
            UserDetails loadedUser = optional.get().loadUserByUsername(username);
            if (loadedUser == null) {
                throw new InternalAuthenticationServiceException(
                        "UserDetailsService returned null, which is an interface contract violation");
            }
            return loadedUser;
        } catch (UsernameNotFoundException ex) {
            mitigateAgainstTimingAttack(authentication);
            throw ex;
        } catch (InternalAuthenticationServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
        }
    }


    private void prepareTimingAttackProtection() {
        if (this.userNotFoundEncodedPassword == null) {
            this.userNotFoundEncodedPassword = this.passwordEncoder.encode(USER_NOT_FOUND_PASSWORD);
        }
    }

    private void mitigateAgainstTimingAttack(UsernamePasswordAuthenticationToken authentication) {
        if (authentication.getCredentials() != null) {
            String presentedPassword = authentication.getCredentials().toString();
            this.passwordEncoder.matches(presentedPassword, this.userNotFoundEncodedPassword);
        }
    }

    /**
     * Sets the PasswordEncoder instance to be used to encode and validate passwords. If
     * not set, the password will be compared using
     * {@link PasswordEncoderFactories#createDelegatingPasswordEncoder()}
     *
     * @param passwordEncoder must be an instance of one of the {@code PasswordEncoder}
     *                        types.
     */
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        Assert.notNull(passwordEncoder, "passwordEncoder cannot be null");
        this.passwordEncoder = passwordEncoder;
        this.userNotFoundEncodedPassword = null;
    }

    protected PasswordEncoder getPasswordEncoder() {
        return this.passwordEncoder;
    }


}
