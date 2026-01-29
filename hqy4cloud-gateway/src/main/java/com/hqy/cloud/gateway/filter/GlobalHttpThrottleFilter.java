package com.hqy.cloud.gateway.filter;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.request.HttpRequestInfo;
import com.hqy.cloud.common.result.R;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.common.swticher.ServerSwitcher;
import com.hqy.cloud.gateway.Constants;
import com.hqy.cloud.gateway.util.RequestUtil;
import com.hqy.cloud.gateway.util.ResponseUtil;
import com.hqy.cloud.gateway.server.GatewayHttpThrottles;
import com.hqy.cloud.limiter.core.LimitResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.aspectj.apache.bcel.generic.RET;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static com.hqy.cloud.common.base.lang.StringConstants.Symbol.AND;
import static com.hqy.cloud.common.base.lang.StringConstants.Symbol.EQUALS;

/**
 * HTTP节流过滤器 判断当前请求是否是安全的、ip是否超限等。
 * @author qiyuan.hong
 * @date  2021-07-27 16:42
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalHttpThrottleFilter implements GlobalFilter, Ordered {
    private final GatewayHttpThrottles httpThrottles;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse httpResponse = exchange.getResponse();
        HttpRequestInfo requestInfo = getHttpRequestInfo(request);
        String uri = requestInfo.getUri();
        String requestIp = requestInfo.getRequestIp();

        // 静态资源 | option请求 | 白名单
        if (RequestUtil.isStaticResource(uri) || request.getMethod() == HttpMethod.OPTIONS ||
                httpThrottles.isWhiteURI(uri) || httpThrottles.isManualWhiteIp(requestIp)) {
            return chain.filter(exchange);
        }

        if (ServerSwitcher.ENABLE_GATEWAY_HTTP_REQUEST_PARAMS_PRINTER.isOn()) {
            log.info("[{}]-[{}]-[{}], params:{}, body:{}", requestInfo.getRequestIp(), requestInfo.getUri(), requestInfo.getMethod(),
                    requestInfo.getRequestParams(), requestInfo.getRequestBody());
        }

        if (ServerSwitcher.ENABLE_HTTP_THROTTLE_SECURITY_CHECKING.isOff()) {
            //没有启用限流器...继续执行责任链
            return chain.filter(exchange);
        } else {
            LimitResult limitResult = httpThrottles.limitValue(requestInfo);
            if (limitResult.isNeedLimit()) {
                String resultTip = StringUtils.isBlank(limitResult.getTip()) ? ResultCode.ILLEGAL_REQUEST_LIMITED.message : limitResult.getTip();
                log.warn("HttpThrottled the request: {}, {}, {}.", requestIp, uri, resultTip);
                R<Object> failed = R.failed(resultTip, ResultCode.NOT_PERMISSION.code);
                return Mono.defer(() -> {
                    DataBuffer buffer = ResponseUtil.outputBuffer(failed, httpResponse, HttpStatus.FORBIDDEN);
                    return httpResponse.writeWith(Flux.just(buffer));
                });
            }
        }
        //继续执行责任链
        return chain.filter(exchange);
    }

    private HttpRequestInfo getHttpRequestInfo(ServerHttpRequest request) {
        return new HttpRequestInfo() {
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
                return request.getMethod().name();
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
    }


    @Override
    public int getOrder() {
        return Constants.GLOBAL_HTTP_THROTTLE_FILER_ORDER;
    }
}
