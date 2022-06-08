package com.hqy.gateway.server;

import com.hqy.access.flow.RedisFlowDTO;
import com.hqy.base.common.base.lang.BaseStringConstants;
import com.hqy.base.common.swticher.HttpGeneralSwitcher;
import com.hqy.coll.service.CollPersistService;
import com.hqy.coll.struct.ThrottledIpBlockStruct;
import com.hqy.foundation.common.HttpRequestInfo;
import com.hqy.foundation.limit.LimitResult;
import com.hqy.foundation.limit.service.HttpThrottles;
import com.hqy.gateway.util.RequestUtil;
import com.hqy.rpc.RPCClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * Http限流器，内部实现了系统忙或者客户端频繁访问时，判定要否限流的功能。也能识别出基本的hack或者数据采集，继而判定要限制访问。<br>
 * 核心实现 依赖 redis 和 google 的CacheBuilder
 * @author qy
 * @date 2021-07-27 19:58
 */
@Slf4j
@Component
public class GatewayHttpThrottles implements HttpThrottles {

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
                    return StringUtils.join(headers, BaseStringConstants.Symbol.COMMA);
                }
                return BaseStringConstants.EMPTY;
            }

            @Override
            public String getRequestParams() {
                return RequestUtil.resolveBodyFromRequest(request);
            }
        };

        return limitValue(requestInfo);
    }


    /**
     * 节流器 是否允许客户端的本次请求
     * @param request
     * @return
     */
    @Override
    public LimitResult limitValue(HttpRequestInfo request) {

        String requestIp = request.getRequestIp();
        String url = StringUtils.isBlank(request.getRequestUrl()) ? request.getUri() : request.getRequestUrl();
        final String errMsg = "Too many requests from [requestIp=" + requestIp + ", url=" + url  + "] ";

        // 是否是人工指定的黑名单阻塞的ip
        if (ThrottlesProcess.getInstance().isManualBlockedIp(requestIp)) {
            log.warn("@@@ MANUAL BLOCKED IP REJECT !!! " + errMsg);
            return new LimitResult(true, errMsg.concat("[MBK]"), LimitResult.ReasonEnum.MANUAL_BLOCKED_IP_NG);
        }
        // 是否是行为分析的黑名单ip
        if (ThrottlesProcess.getInstance().isBiBlockedIp(requestIp)) {
            log.warn("@@@ BI BLOCKED IP REJECT !!! " + errMsg);
            return new LimitResult(true, errMsg.concat("[BBK]"), LimitResult.ReasonEnum.BI_BLOCKED_IP_NG);
        }
        //是否校验请求中的xss 聚合浓缩黑客判定方法
        if (HttpGeneralSwitcher.ENABLE_HTTP_THROTTLE_SECURITY_CHECKING.isOn()) {
            LimitResult hackCheckLimitResult;
            if (url.contains(BaseStringConstants.Symbol.QUESTION_MARK)) {
                //有?就走正常的url校验
                hackCheckLimitResult = checkHackAccess(request.getRequestParams(), requestIp, request.getUri(), url);
            } else {
                //防止双引号脚本的内容攻击....
                hackCheckLimitResult = checkHackAccess(request.getRequestParams(),requestIp,request.getUri(), request.getRequestUrl());
            }
            if (hackCheckLimitResult.isNeedLimit()) {
                hackCheckLimitResult.setTip(errMsg.concat("[HAK]"));
                return hackCheckLimitResult;
            }
        }

        if (HttpGeneralSwitcher.ENABLE_HTTP_THROTTLE_VALVE.isOff()) {
            //未开启限流器
            return new LimitResult(false, "HttpGeneralSwitcher.ENABLE_HTTP_THROTTLE_VALVE.isOff", LimitResult.ReasonEnum.NOT_ENABLE_HTTP_THROTTLE_OK);
        } else {
            try {
                boolean needLimit;
                if (HttpGeneralSwitcher.ENABLE_SHARE_IP_OVER_REQUEST_STATISTICS.isOff()) {
                    // 基于本地Cache的技术方式, gateway如果是集群情况下 可能不准确！
                    // TODO 根据节点信息， 从guava缓存中判断当前ip是否超限
                    needLimit = false;
                } else {
                    //基于redis的共享统计方式 计数更准确！
                    RedisFlowDTO redisFlowDTO = RedisFlowControlCenter.INSTANCE.needLimitPerTimeWindow(requestIp, request.getMethod(), request.getUri());
                    needLimit = redisFlowDTO.getOverLimit();
                    if (needLimit && redisFlowDTO.getBlockSeconds() > 0) {
                        ThrottlesProcess.getInstance().addBiBlockIp(requestIp, redisFlowDTO.getBlockSeconds());
                        // 记录ip 被阻塞 持久化到数据库
                        persistBlockIpAction(requestIp, redisFlowDTO.getBlockSeconds(), url, "BiBlock(RedisFlowControl)", request.getUri());
                    }
                }
                if (needLimit) {
                    //TODO 当前ip超限了 是不是可以报警一下?
                    log.warn("@@@ RATE LIMIT , too fast access !!! " + errMsg);
                    return new LimitResult(true, errMsg, LimitResult.ReasonEnum.RATE_LIMIT_NG);
                }
            } catch (Exception e) {
                log.error("Throttles HTTP request [requestIp=" + requestIp + "] failed", e);
            }

        }
        return new LimitResult();
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
    public LimitResult checkHackAccess(String requestParams, String requestIp, String uri, String urlOrQueryString) {

        //uri和请求参数的xss攻击校验
        if (HttpGeneralSwitcher.ENABLE_HTTP_THROTTLE_SECURITY_CHECKING.isOn()) {
            if (StringUtils.isNotBlank(requestParams)) {
                boolean hackAccess = ThrottlesProcess.getInstance().isHackAccess(requestParams, ThrottlesProcess.PARAMS_CHECK_MODE);
                if (hackAccess) {
                    log.warn("@@@ HACK TOOL REJECT (param) !!! {} , {} ", requestParams, requestIp);
                    // 纳入黑名单，访问限制!!!!
                    if (HttpGeneralSwitcher.ENABLE_IP_RATE_LIMIT_HACK_CHECK_RULE.isOff()) {
                        //1次访问有问题，就拉黑
                        ThrottlesProcess.getInstance().addManualBlockIp(requestIp, ThrottlesProcess.IP_ACCESS_BLOCK_SECONDS);
                    }
                    // 记录ip 被阻塞 持久化服务~
                    persistBlockIpAction(requestIp, ThrottlesProcess.IP_ACCESS_BLOCK_SECONDS, urlOrQueryString, "BiBlock(HackAccessParam)", requestParams);
                    return new LimitResult(true, LimitResult.ReasonEnum.HACK_TOOL_ACCESS_NG);
                }
            }

            if (StringUtils.isNotBlank(urlOrQueryString) && urlOrQueryString.length() > 16) {
                boolean hackAccess = ThrottlesProcess.getInstance().isHackAccess(urlOrQueryString, ThrottlesProcess.URI_CHECK_MODE);
                if (hackAccess) {
                    log.warn("@@@ HACK TOOL REJECT (urlOrQueryString)!!! {} ,remoteAddr:{} ", urlOrQueryString, requestIp);
                    // 纳入黑名单，访问限制!!!!
                    if (HttpGeneralSwitcher.ENABLE_IP_RATE_LIMIT_HACK_CHECK_RULE.isOff()) {
                        //1次访问有问题，就拉黑
                        ThrottlesProcess.getInstance().addManualBlockIp(requestIp, ThrottlesProcess.IP_ACCESS_BLOCK_SECONDS);
                    }
                    // 记录ip 被阻塞 持久化服务~
                    persistBlockIpAction(requestIp, ThrottlesProcess.IP_ACCESS_BLOCK_SECONDS, urlOrQueryString, "BiBlock(HackAccessURI)", urlOrQueryString);
                    return new LimitResult(true, LimitResult.ReasonEnum.HACK_TOOL_ACCESS_NG);
                }
            }

            if (StringUtils.isNotBlank(uri) && !BaseStringConstants.Symbol.INCLINED_ROD.equals(uri)) {
                boolean hackAccess = ThrottlesProcess.getInstance().isHackAccess(uri, ThrottlesProcess.URI_CHECK_MODE);
                if (hackAccess) {
                    log.warn("@@@ HACK TOOL REJECT (URI)!!!,{}, requestIp:{} ", uri, requestIp);
                    if (HttpGeneralSwitcher.ENABLE_IP_RATE_LIMIT_HACK_CHECK_RULE.isOff()) {
                        //1次访问有问题，就拉黑
                        ThrottlesProcess.getInstance().addManualBlockIp(requestIp, ThrottlesProcess.IP_ACCESS_BLOCK_SECONDS);
                    }
                    // 记录ip 被阻塞 持久化服务~
                    persistBlockIpAction(requestIp, ThrottlesProcess.IP_ACCESS_BLOCK_SECONDS, urlOrQueryString, "BiBlock(HackAccessURI)", urlOrQueryString);
                    return new LimitResult(true, LimitResult.ReasonEnum.HACK_TOOL_ACCESS_NG);
                }
            }

        }
        return new LimitResult();
    }

    /**
     * 是否是白名单uri
     * @param uri
     * @return
     */
    @Override
    public boolean isWhiteURI(String uri) {
        return ThrottlesProcess.getInstance().isWhiteIp(uri);
    }


    @Override
    public boolean isManualWhiteIp(String remoteAddr) {
        return ThrottlesProcess.getInstance().isWhiteIp(remoteAddr);
    }


    private static final int MAX_LENGTH = 1024;

    /**
     * 记录封禁 行为日志，历史记录，方便将来查看...
     * 将有问题的ip 通过消息队列进行数据的异步持久化...
     * @param ip 被封禁的ip
     * @param blockSeconds 被堵塞的时间时长，秒
     * @param url 被拦截时的访问url  例如是人工指定？还是HttpThrottleFilter(发现了恶意访问)，还是BIBlock(恶意关键词等..)
     * @param createdBy 阻塞操作的组件或者逻辑
     * @param accessParamJson 请求参数json
     */
    public void persistBlockIpAction(String ip, Integer blockSeconds, String url, String createdBy, String accessParamJson) {

        if (HttpGeneralSwitcher.ENABLE_HTTP_THROTTLE_PERSISTENCE.isOff()) {
            log.warn("ignore persistThrottleInfo: {},{}, reason:{}", ip, url, createdBy);
            return;
        }

        ThrottledIpBlockStruct struct = new ThrottledIpBlockStruct();
        if (StringUtils.isNotBlank(accessParamJson) && accessParamJson.length() > MAX_LENGTH) {
            //只截取前1024个字符的提示信息,太长了就丢掉
            accessParamJson = accessParamJson.substring(0, MAX_LENGTH);
        }
        struct.ip = ip;
        struct.accessJson = accessParamJson;
        struct.blockedSeconds = blockSeconds;
        struct.url = url;
        struct.throttleBy = createdBy;

        CollPersistService remoteService = RPCClient.getRemoteService(CollPersistService.class);
        remoteService.saveThrottledIpBlockHistory(struct);

    }
}
