package com.hqy.gateway.server;

import cn.hutool.core.map.MapUtil;
import com.hqy.access.auth.UploadFileSecurityChecker;
import com.hqy.access.flow.FlowResult;
import com.hqy.access.flow.server.HttpAccessFlowControlCenter;
import com.hqy.base.common.base.lang.StringConstants;
import com.hqy.base.common.swticher.HttpGeneralSwitcher;
import com.hqy.coll.enums.BiBlockType;
import com.hqy.coll.service.CollPersistService;
import com.hqy.coll.struct.ThrottledBlockStruct;
import com.hqy.foundation.common.HttpRequestInfo;
import com.hqy.foundation.limit.LimitResult;
import com.hqy.foundation.limit.service.HttpThrottles;
import com.hqy.gateway.util.RequestUtil;
import com.hqy.rpc.nacos.client.starter.RPCClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;

import static com.hqy.base.common.base.lang.StringConstants.Symbol.*;

/**
 * Http限流器，内部实现了系统忙或者客户端频繁访问时，判定要否限流的功能。也能识别出基本的hack或者数据采集，继而判定要限制访问。<br>
 * 核心实现依赖 redis zset的滑动窗口算法和 google的RateLimiter的平滑突发限流 令牌桶算法
 * @author qy
 * @date 2021-07-27 19:58
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GatewayHttpThrottles implements HttpThrottles {

    private final HttpAccessFlowControlCenter flowControlCenter;
    private final ThrottlesProcess throttlesProcess;
    private final UploadFileSecurityChecker uploadFileSecurityChecker;
    private static final int MAX_LENGTH = 1024;

    public LimitResult limitValue(ServerHttpRequest request) {

        final HttpRequestInfo requestInfo = new HttpRequestInfo() {
            @Override
            public String getUri() {
                return request.getURI().getPath();
            }

            @Override
            public String getRequestUrl() {
                return request.getURI().toString();
            }

            @Override
            public String getMethod() {
                return request.getMethodValue();
            }

            @Override
            public String getRequestIp() {
                return RequestUtil.getIpAddress(request);
            }

            @Override
            public String getIpCountry() {
                return null;
            }

            @Override
            public String getHeader(String header) {
                List<String> headers = request.getHeaders().get(header);
                if (CollectionUtils.isNotEmpty(headers)) {
                    return StringUtils.join(headers, StringConstants.Symbol.COMMA);
                }
                return StringConstants.EMPTY;
            }

            @Override
            public String getRequestParams() {
                MultiValueMap<String, String> queryParams = request.getQueryParams();
                if (MapUtil.isNotEmpty(queryParams)) {
                    StringBuilder sb = new StringBuilder();
                    for (Map.Entry<String, String> entry : queryParams.toSingleValueMap().entrySet()) {
                        sb.append(entry.getKey())
                                .append(EQUALS)
                                .append(entry.getValue())
                                .append(AND);
                    }
                    return sb.toString();
                } else {
                    return StringConstants.EMPTY;
                }
            }

            @Override
            public String getRequestBody() {
                return RequestUtil.resolveBodyFromRequest(request);
            }
        };

        return limitValue(requestInfo);
    }

    @Override
    public LimitResult limitValue(HttpRequestInfo request) {
        String uri = request.getUri();
        String requestIp = request.getRequestIp();
        String requestUrl = request.getRequestUrl();
        String requestParams = request.getRequestParams();
        String url = StringUtils.isBlank(requestUrl) ? uri : requestUrl;

        // 是否是人工指定的黑名单阻塞的ip
        if (throttlesProcess.isManualBlockedIp(requestIp)) {
            return new LimitResult(true, printErrorMessage(requestIp, url, "[MBK]"), LimitResult.ReasonEnum.MANUAL_BLOCKED_IP_NG);
        }

        // 是否是行为分析的黑名单ip
        if (throttlesProcess.isBiBlockedIp(requestIp)) {
            return new LimitResult(true, printErrorMessage(requestIp, url, "[BBK]"), LimitResult.ReasonEnum.BI_BLOCKED_IP_NG);
        }

        //是否校验请求中的xss 聚合浓缩黑客判定方法
        if (HttpGeneralSwitcher.ENABLE_HTTP_THROTTLE_SECURITY_CHECKING.isOn() &&
                !uploadFileSecurityChecker.isUploadFileRequest(request.getHeader(HttpHeaders.CONTENT_TYPE), uri)) {
            LimitResult hackCheckLimitResult = checkHackAccess(uri, StringUtils.isBlank(requestParams) ? url : requestParams , request.getRequestBody(), requestIp);
            if (hackCheckLimitResult.isNeedLimit()) {
                hackCheckLimitResult.setTip(printErrorMessage(requestIp, url, "[HAK]"));
                return hackCheckLimitResult;
            }
        }

        if (HttpGeneralSwitcher.ENABLE_HTTP_THROTTLE_VALVE.isOff()) {
            return new LimitResult(false, null, LimitResult.ReasonEnum.NOT_ENABLE_HTTP_THROTTLE_OK);
        } else {
            try {
                FlowResult flowResult = flowControlCenter.needLimitPerTimeWindow(requestIp, request.getMethod(), uri);
                boolean needLimit = flowResult.isOverLimit();
                if (flowResult.isBlock()) {
                    throttlesProcess.addBiBlockIp(requestIp, flowResult.getBlockSeconds());
                    persistBlockIpAction(requestIp, flowResult.getBlockSeconds(), url, BiBlockType.REDIS_FLOW.value, request.getRequestBody());
                }
                if (needLimit) {
                    return new LimitResult(true, printErrorMessage(requestIp, url, ""), LimitResult.ReasonEnum.RATE_LIMIT_NG);
                }
            } catch (Exception e) {
                log.error("Throttles HTTP request [requestIp=" + requestIp + "] failed", e);
            }

        }
        return new LimitResult();
    }

    private String printErrorMessage(String requestIp, String url, String mode) {
        return "Too many requests from [requestIp=" + requestIp + ", url=" + url + "] " + mode;
    }


    @Override
    public LimitResult checkHackAccess(String uri, String urlOrQueryString, String requestBody, String requestIp) {
        //聚合浓缩黑客判定方法....
        if (HttpGeneralSwitcher.ENABLE_HTTP_THROTTLE_SECURITY_CHECKING.isOn()) {
            //检查请求体
            if (StringUtils.isNotBlank(requestBody)) {
                if (throttlesProcess.isHackAccess(requestBody, ThrottlesProcess.PARAMS_CHECK_MODE)) {
                    return limitHackAccessAndPersistBlockIp(requestIp, urlOrQueryString, BiBlockType.HACK_ACCESS_PARAM.value, requestBody);
                }
            }

            //检查url或请求参数
            if (StringUtils.isNotBlank(urlOrQueryString)) {
                if (throttlesProcess.isHackAccess(urlOrQueryString, ThrottlesProcess.URI_CHECK_MODE)) {
                    return limitHackAccessAndPersistBlockIp(requestIp, urlOrQueryString, BiBlockType.HACK_ACCESS_URI.value, requestBody);
                }
            }

            //检查uri
            if (StringUtils.isNotBlank(uri) && !INCLINED_ROD.equals(uri)) {
                if (throttlesProcess.isHackAccess(uri, ThrottlesProcess.URI_CHECK_MODE)) {
                    if (HttpGeneralSwitcher.ENABLE_IP_RATE_LIMIT_HACK_CHECK_RULE.isOff()) {
                        return limitHackAccessAndPersistBlockIp(requestIp, urlOrQueryString, BiBlockType.HACK_ACCESS_URI.value, requestBody);
                    }
                }
            }
        }
        return new LimitResult();
    }

    private LimitResult limitHackAccessAndPersistBlockIp(String requestIp, String url, String createdBy, String requestBody) {
        if (HttpGeneralSwitcher.ENABLE_IP_RATE_LIMIT_HACK_CHECK_RULE.isOff()) {
            // 纳入黑名单，访问限制!!!!
            throttlesProcess.addManualBlockIp(requestIp, ThrottlesProcess.IP_ACCESS_BLOCK_SECONDS);
        }
        // 记录ip 被阻塞 持久化服务
        persistBlockIpAction(requestIp, ThrottlesProcess.IP_ACCESS_BLOCK_SECONDS, url, createdBy, requestBody);
        return new LimitResult(true, LimitResult.ReasonEnum.HACK_TOOL_ACCESS_NG);
    }

    @Override
    public boolean isWhiteURI(String uri) {
        return throttlesProcess.isWhiteUri(uri);
    }


    @Override
    public boolean isManualWhiteIp(String remoteAddr) {
        return throttlesProcess.isWhiteIp(remoteAddr);
    }




    /**
     * 记录封禁 行为日志，历史记录，方便将来查看...
     * 将有问题的ip 通过消息队列进行数据的异步持久化...
     * @param ip              被封禁的ip
     * @param blockSeconds    被堵塞的时间时长，秒
     * @param url             被拦截时的访问url  例如是人工指定？还是HttpThrottleFilter(发现了恶意访问)，还是BIBlock(恶意关键词等..)
     * @param createdBy       阻塞操作的组件或者逻辑
     * @param accessParamJson 请求参数json
     */
    public void persistBlockIpAction(String ip, Integer blockSeconds, String url, String createdBy, String accessParamJson) {

        if (HttpGeneralSwitcher.ENABLE_HTTP_THROTTLE_PERSISTENCE.isOff()) {
            log.warn("Ignore persistThrottleInfo: {}, {}, reason:{}", ip, url, createdBy);
            return;
        }

        if (StringUtils.isNotBlank(accessParamJson) && accessParamJson.length() > MAX_LENGTH) {
            //只截取前1024个字符的提示信息,太长了就丢掉
            accessParamJson = accessParamJson.substring(0, MAX_LENGTH);
        }

        ThrottledBlockStruct struct = new ThrottledBlockStruct();
        struct.ip = ip;
        struct.accessJson = accessParamJson;
        struct.blockedSeconds = blockSeconds;
        struct.url = url;
        struct.throttleBy = createdBy;
        CollPersistService remoteService = RPCClient.getRemoteService(CollPersistService.class);
        remoteService.saveThrottledBlockHistory(struct);
    }
}
