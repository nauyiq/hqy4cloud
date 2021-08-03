package com.hqy.limit.impl;

import com.hqy.common.HttpRequestInfo;
import com.hqy.common.swticher.HttpGeneralSwitcher;
import com.hqy.dto.LimitResult;
import com.hqy.limit.BiBlockedIpService;
import com.hqy.limit.HttpThrottles;
import com.hqy.limit.ManualBlockedIpService;
import com.hqy.util.spring.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Http限流器，内部实现了系统忙或者客户端频繁访问时，判定要否限流的功能。也能识别出基本的hack或者数据采集，继而判定要限制访问。<br>
 * 核心实现 依赖google 的CacheBuilder
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-07-27 19:58
 */
@Slf4j
public class GateWayHttpThrottles implements HttpThrottles {

    /**
     * 是否允许本次客户端请求
     * 针对WEB Flux的http请求封装
     * @param request
     * @return
     */
    public LimitResult limitValue(ServerHttpRequest request) {

        final HttpRequestInfo requestInfo = new HttpRequestInfo() {
            @Override
            public String getUri() {
                return request.getURI().getPath();
            }

            @Override
            public String getRequestUrl() {
                return request.getPath().pathWithinApplication().value();
            }

            @Override
            public String getMethod() {
                return request.getMethodValue();
            }

            @Override
            public String getRequestIp() {
                return Objects.requireNonNull(request.getRemoteAddress()).getAddress().getHostAddress();
            }

            @Override
            public String getIpCountry() {
                return null;
            }

            @Override
            public String getHeader(String header) {
                List<String> headers = request.getHeaders().get(header);
                if (CollectionUtils.isNotEmpty(headers)) {
                    return StringUtils.join(headers, ",");
                }
                return null;
            }

            @Override
            public Map<String, String> getRequestParams() {
                return request.getQueryParams().toSingleValueMap();
            }
        };

        return limitValue(requestInfo);
    }


    @Override
    public LimitResult limitValue(HttpRequestInfo request) {

        //TODO 服务刚启动...根据环境进行放行规则...

        //TODO 服务刚启动...根据环境进行放行规则...

        String requestIp = request.getRequestIp();
        String url = request.getRequestUrl();
        if (StringUtils.isBlank(url)) {
            url = request.getUri();
        }

        final String errMsg = "Too many requests from [remoteAddr=" + requestIp + ", url=" + url  + "] ";

        // 是否是人工指定的黑名单阻塞的ip
        if (isManualBlockedIp(requestIp)) {
            log.warn("@@@ MANUAL BLOCKED IP REJECT !!! " + errMsg);
            return new LimitResult(true,"isManualBlockedIp", LimitResult.ReasonEum.MANUAL_BLOCKED_IP_NG);
        }
        // 是否是行为分析的黑名单ip
        if (isBIBlockedIp(requestIp)) {
            log.warn("@@@ BI BLOCKED IP REJECT !!! " + errMsg);
            return new LimitResult(true, "isBIBlockedIp", LimitResult.ReasonEum.BI_BLOCKED_IP_NG);
        }

        if (HttpGeneralSwitcher.ENABLE_HTTP_THROTTLE_SECURITY_CHECKING.isOn()) {
            LimitResult hackCheckLimitResult;
            //聚合浓缩黑客判定方法
            if (url.contains("?")) { //有?就走正常的url校验
                hackCheckLimitResult = checkHackAccess(request.getRequestParams(), requestIp, request.getUri(), url);
            }
        }

        return null;
    }

    /**
     * 检查是否是黑客访问.....
     * 聚合浓缩黑客判定方法....
     * @param requestParams
     * @param requestIp
     * @param uri
     * @param urlOrQueryString
     * @return
     */
    @Override
    public LimitResult checkHackAccess(@SuppressWarnings("rawtypes")Map requestParams, String requestIp, String uri, String urlOrQueryString) {


        return null;
    }


    /**
     * 是否是bi分析后的拒绝访问的黑名单？
     * @param requestIp
     * @return
     */
    private static boolean isBIBlockedIp(String requestIp) {
        BiBlockedIpService service = SpringContextHolder.getBean(RedisBiBlockedIpService.class);
        return service.isBlockIp(requestIp);
    }


    /**
     * 是否是人工指定的拒绝访问的黑名单ip?
     * @param requestIp
     * @return
     */
    private static boolean isManualBlockedIp(String requestIp) {
        ManualBlockedIpService service = SpringContextHolder.getBean(RedisManualBlockedIpService.class);
        return service.isBlockIp(requestIp);
    }
}
