package com.hqy.limit.impl;

import com.hqy.common.HttpRequestInfo;
import com.hqy.common.swticher.HttpGeneralSwitcher;
import com.hqy.dto.LimitResult;
import com.hqy.global.flow.RedisFlowControlCenter;
import com.hqy.global.flow.RedisFlowDTO;
import com.hqy.limit.HttpThrottles;
import com.hqy.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

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


    /**
     * 节流器 是否允许客户端的本次请求
     * @param request
     * @return
     */
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
        if (ThrottlesProcess.getInstance().isManualBlockedIp(requestIp)) {
            log.warn("@@@ MANUAL BLOCKED IP REJECT !!! " + errMsg);
            return new LimitResult(true, errMsg.concat("[MBK]"), LimitResult.ReasonEnum.MANUAL_BLOCKED_IP_NG);
        }
        // 是否是行为分析的黑名单ip
        if (ThrottlesProcess.getInstance().isBIBlockedIp(requestIp)) {
            log.warn("@@@ BI BLOCKED IP REJECT !!! " + errMsg);
            return new LimitResult(true, errMsg.concat("[BBK]"), LimitResult.ReasonEnum.BI_BLOCKED_IP_NG);
        }

        if (HttpGeneralSwitcher.ENABLE_HTTP_THROTTLE_SECURITY_CHECKING.isOn()) {
            LimitResult hackCheckLimitResult;
            //聚合浓缩黑客判定方法
            if (url.contains("?")) { //有?就走正常的url校验
                hackCheckLimitResult = checkHackAccess(request.getRequestParams(), requestIp, request.getUri(), url);
            } else {
                //防止双引号脚本的内容攻击....
                hackCheckLimitResult =  checkHackAccess(request.getRequestParams(),requestIp,request.getUri(), request.getRequestUrl());
            }
            if (hackCheckLimitResult.isNeedLimit()) {
                hackCheckLimitResult.setTip(errMsg.concat("[HAK]"));
                return hackCheckLimitResult;
            }
        }

        //TODO CHECK DB BUSY?

        //TODO CHECK DB BUSY?

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
                        ThrottlesProcess.getInstance().persistBlockIpAction(requestIp, redisFlowDTO.getBlockSeconds(), url, "BiBlock(RedisFlowControl)", request.getUri());
                    }
                }

                if (needLimit) {
                    //TODO 当前ip超限了 是不是可以报警一下?
                    log.warn("@@@ RATE LIMIT , too fast access !!! " + errMsg);
                    return new LimitResult(true, errMsg, LimitResult.ReasonEnum.RATE_LIMIT_NG);
                }

            } catch (Exception e) {
                //TODO 异常采集
                log.error("Throttles HTTP request [remoteAddr=" + requestIp + "] failed", e);
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
    public LimitResult checkHackAccess(@SuppressWarnings("rawtypes")Map requestParams, String requestIp, String uri, String urlOrQueryString) {

        if (HttpGeneralSwitcher.ENABLE_HTTP_THROTTLE_SECURITY_CHECKING.isOn() && Objects.nonNull(requestParams)) {
            if (!requestParams.isEmpty()) {
                String paramString = JsonUtil.toJson(requestParams);
                boolean hackAccess = ThrottlesProcess.getInstance().isHackAccess(paramString, ThrottlesProcess.PARAMS_CHECK_MODE);
                if (hackAccess) {
                    log.warn("@@@ HACK TOOL REJECT (param) !!! {} , {} ", paramString, requestIp);
                    // 纳入黑名单，访问限制!!!!
                    if (HttpGeneralSwitcher.ENABLE_IP_RATE_LIMIT_HACK_CHECK_RULE.isOff()) {
                        //1次访问有问题，就拉黑
                        ThrottlesProcess.getInstance().addBiBlockIp(requestIp, ThrottlesProcess.IP_ACCESS_BLOCK_SECONDS);
                    }
                    // 记录ip 被阻塞 持久化服务~
                    ThrottlesProcess.getInstance().
                            persistBlockIpAction(requestIp, ThrottlesProcess.IP_ACCESS_BLOCK_SECONDS, urlOrQueryString, "BiBlock(HackAccessParam)", paramString);
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
                        ThrottlesProcess.getInstance().addBiBlockIp(requestIp, ThrottlesProcess.IP_ACCESS_BLOCK_SECONDS);
                    }
                    // 记录ip 被阻塞 持久化服务~
                    ThrottlesProcess.getInstance().
                            persistBlockIpAction(requestIp, ThrottlesProcess.IP_ACCESS_BLOCK_SECONDS, urlOrQueryString, "BiBlock(HackAccessURI)", urlOrQueryString);
                    return new LimitResult(true, LimitResult.ReasonEnum.HACK_TOOL_ACCESS_NG);
                }
            }

            if (StringUtils.isNotBlank(uri) && !"/".equals(uri)) {
                boolean hackAccess = ThrottlesProcess.getInstance().isHackAccess(uri, ThrottlesProcess.URI_CHECK_MODE);
                if (hackAccess) {
                    log.warn("@@@ HACK TOOL REJECT (URI)!!!,{}, remoteAddr:{} ", uri, requestIp);
                    if (HttpGeneralSwitcher.ENABLE_IP_RATE_LIMIT_HACK_CHECK_RULE.isOff()) {
                        //1次访问有问题，就拉黑
                        ThrottlesProcess.getInstance().addBiBlockIp(requestIp, ThrottlesProcess.IP_ACCESS_BLOCK_SECONDS);
                    }
                    // 记录ip 被阻塞 持久化服务~
                    ThrottlesProcess.getInstance().
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
        //TODO 可从配置文件加载...
        return false;
    }

    @Override
    public boolean isManualWhiteIp(String remoteAddr) {
        return ThrottlesProcess.getInstance().isManualBlockedIp(remoteAddr);
    }
}
