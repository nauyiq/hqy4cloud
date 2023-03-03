package com.hqy.web.global;

import cn.hutool.core.map.MapUtil;
import com.hqy.account.struct.AccountBaseInfoStruct;
import com.hqy.cloud.util.OauthRequestUtil;
import com.hqy.web.service.account.AccountRpcUtil;
import com.hqy.web.service.account.dto.AccountPayloadDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

/**
 * BaseController.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/9 9:51
 */
public abstract class BaseController {
    private static final Logger log = LoggerFactory.getLogger(BaseController.class);

    public static HttpServletRequest getRequest(){
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }

    public String getAuthorizationFromRequest() {
        return getAuthorizationFromRequest(getRequest());
    }

    public String getAuthorizationFromRequest(HttpServletRequest request) {
        return OauthRequestUtil.requestPayloadFromOauth2Header(request);
    }

    public Long getAccessAccountId() {
        return getAccessAccountId(getRequest());
    }

    public Long getAccessAccountId(HttpServletRequest request) {
        return OauthRequestUtil.idFromOauth2Request(request);
    }

    public AccountPayloadDTO getAccessAccountPayload() {
        return getAccessAccountPayload(getRequest());
    }

    public AccountPayloadDTO getAccessAccountPayload(HttpServletRequest request) {
        Map<String, Object> map = OauthRequestUtil.requestMapFromOauth2Payload(request);
        return new AccountPayloadDTO(MapUtil.getLong(map, "id"),
                MapUtil.getStr(map, "password"), MapUtil.getStr(map, "email"), MapUtil.getStr(map, "username"));
    }


    public AccountBaseInfoStruct getAccessAccountBaseInfo(HttpServletRequest request) {
        Long accountId = getAccessAccountId(request);
        return AccountRpcUtil.getAccountBaseInfo(accountId);
    }





}
