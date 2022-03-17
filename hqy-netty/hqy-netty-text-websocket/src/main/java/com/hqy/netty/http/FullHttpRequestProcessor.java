package com.hqy.netty.http;

import com.hqy.fundation.common.base.lang.BaseStringConstants;
import com.hqy.fundation.common.result.CommonResultCode;
import com.hqy.util.AssertUtil;
import com.hqy.util.IpUtil;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 解析netty自带的FullRequest
 * @author qiyuan.hong
 * @date 2022-03-08 23:06
 */
public class FullHttpRequestProcessor {

    private static final Logger log = LoggerFactory.getLogger(FullHttpRequestProcessor.class);

    /**
     * 保存FullRequest解析的参数
     */
    private final Map<String, String> params = new HashMap<>();

    /**
     * 请求ip
     */
    private String remoteIp;

    public FullHttpRequestProcessor(FullHttpRequest request) {
        parse(request);
    }

    /**
     * 解析FullHttpRequest
     * @param request FullHttpRequest
     */
    private void parse(FullHttpRequest request) {
        AssertUtil.notNull(request, CommonResultCode.INVALID_DATA.message);

        HttpMethod method = request.method();
        if (method == HttpMethod.GET) {
            //get请求解析uri即可
            String uri = request.uri();
            parseQueryString(uri);
        }

        String requestIp = getRequestIp(request);
        if (StringUtils.isBlank(requestIp)) {
            requestIp = getRemoteIp();
        }
        this.remoteIp = requestIp;
    }


    /**
     * 获取对端连接的ip
     * @param request FullHttpRequest
     * @return 对端ip
     */
    public String getRemoteIp(FullHttpRequest request) {
        String ip = request.headers().get("cf-connecting-ip");
        if (StringUtils.isNotEmpty(ip)) {
            return ip;
        }
        String xForwardedFor = request.headers().get("X-Forwarded-For");
        ip = request.headers().get("X-Real-IP");
        if (StringUtils.isNotEmpty(ip)) {
            log.info("### Use X-Real-IP.");
        } else {
            ip = xForwardedFor.split(",")[0].trim();
        }
        return ip;
    }


    /**
     * 解析uri中的参数 即 ? 后面的参数
     * @param uri 请求uri
     */
    private void parseQueryString(String uri) {
        if (!uri.contains(BaseStringConstants.Symbol.QUESTION_MARK)) {
            log.info("@@@ FullHttpRequestProcessor.parseQueryString, uri not contain '?'. uri:{}", uri);
            return;
        }
        //获取请求?后的数据
        String params = uri.split("\\?")[1];
        if (StringUtils.isNoneBlank(params)) {
            String[] strings = params.split("&");
            for (String param : strings) {
                int equals = param.indexOf("=");
                if (equals > 0) {
                    String name = param.substring(0, equals);
                    String value = param.substring(equals + 1);
                    try {
                        this.params.put(name, URLDecoder.decode(value, "utf-8"));
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                } else {
                    this.params.put(param, null);
                }
            }
        }
    }


    /**
     * 通过FullHttpRequest获取对端连接的ip
     * @param request FullHttpRequest
     * @return 对端ip
     */
    public static String getRequestIp(FullHttpRequest request) {
        //cf-connecting-ip
        //一些 CDN 会在 CDN 回源请求加 HTTP 头信息，其中可能包含访客的“真实 IP ”。具体要看是哪家 CDN ，比如 Cloudflare 是 CF-Connecting-IP
        String ip = request.headers().get("cf-connecting-ip");
        if (checkIpString(ip)) {
            return ip;
        }
        //X-Real-IP
        ip = request.headers().get("X-Real-IP");
        if (checkIpString(ip)) {
            return ip;
        }
        //Proxy-Client-IP
        ip = request.headers().get("Proxy-Client-IP");
        if (checkIpString(ip)) {
            return ip;
        }
        //WL-Proxy-Client-IP
        ip = request.headers().get("WL-Proxy-Client-IP");
        if (checkIpString(ip)) {
            return ip;
        }
        //HTTP_CLIENT_IP
        ip = request.headers().get("HTTP_CLIENT_IP");
        if (checkIpString(ip)) {
            return ip;
        }
        //HTTP_X_FORWARDED_FOR
        ip = request.headers().get("HTTP_X_FORWARDED_FOR");
        if (checkIpString(ip)) {
            return ip;
        }
        return null;
    }

    private static boolean checkIpString(String ip) {
        return StringUtils.isNotEmpty(ip) && !IpUtil.isInnerIp(ip) && !BaseStringConstants.UNKNOWN.equalsIgnoreCase(ip);
    }


    public Map<String, String> getParams() {
        return params;
    }

    public String getRemoteIp() {
        return remoteIp;
    }
}
