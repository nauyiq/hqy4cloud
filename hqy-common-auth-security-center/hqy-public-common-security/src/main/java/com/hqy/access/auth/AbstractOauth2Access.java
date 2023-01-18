package com.hqy.access.auth;

import com.hqy.access.auth.support.EndpointAuthorizationManager;
import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AbstractOath2Access.
 * @see Oauth2Access
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/10 14:02
 */
public abstract class AbstractOauth2Access implements Oauth2Access {
    private static final Logger log = LoggerFactory.getLogger(AbstractOauth2Access.class);

    @Override
    public boolean isPermitRequest(AuthenticationRequest request) {
        if (request == null) {
            log.warn("Oath2 request should not be null. checking false.");
            return false;
        }

        String requestUri = request.requestUri();

        //是否是静态的端点访问uri
        if (isWhiteStaticEndpoint(requestUri)) {
            return true;
        }
        //判断是否是业务允许通过的uri
        if (isWhiteAccessUri(requestUri)) {
            return true;
        }
        //判断是否是白名单IP
        if (isWhiteAccessIp(request.requestIp())) {
            return true;
        }
        //判断是否携带token.
//        if (isLegalAccessToken(request.requestAccessToken())) {
//            return true;
//        }


        log.info("Access request not Permit, this request: {}", JsonUtil.toJson(request));

        return false;
    }



    /**
     * 是否是白名单ip
     * @param requestIp 请求ip
     * @return          result.
     */
    protected abstract boolean isWhiteAccessIp(String requestIp);

    /**
     * 是否是白名单uri
     * @param requestUri request uri
     * @return           result.
     */
    protected abstract boolean isWhiteAccessUri(String requestUri);

    protected boolean isLegalAccessToken(String requestAccessToken) {
        return StringUtils.isNotBlank(requestAccessToken) && StringUtils.startsWithIgnoreCase(requestAccessToken, StringConstants.Auth.JWT_PREFIX);
    }

    protected boolean isWhiteStaticEndpoint(String requestUri) {
        if (StringUtils.isBlank(requestUri)) {
            return false;
        }
        return EndpointAuthorizationManager.getInstance().isStaticWhiteEndpoint(requestUri);
    }
}
