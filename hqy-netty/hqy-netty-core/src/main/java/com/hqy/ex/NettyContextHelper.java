package com.hqy.ex;

import com.hqy.base.common.base.lang.BaseMathConstants;
import com.hqy.base.common.base.lang.BaseStringConstants;
import com.hqy.base.common.base.project.MicroServiceConstants;
import com.hqy.util.JwtUtil;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.springframework.util.StringUtils;

/**
 * @author qiyuan.hong
 * @date 2022-03-18 09:59
 */
public class NettyContextHelper {

    private NettyContextHelper() {}

    public static boolean isInnerIp(String ip) {
        return BaseStringConstants.INNER_IP.equals(ip);
    }


    /**
     * 允许跨域，回写跨域相关响应头
     * @param httpResponse httpResponse
     */
    public static void allowCors(HttpResponse httpResponse, String origin) {
        if (org.apache.commons.lang3.StringUtils.isNotBlank(origin)) {
            httpResponse.headers().set("access-control-allow-origin", origin);
        } else {
            httpResponse.headers().set("access-control-allow-origin", "*");
        }
        httpResponse.headers().set("Access-Control-Allow-Credentials",true);
        httpResponse.headers().add("Access-Control-Allow-Headers", "*");
        httpResponse.headers().add("Referrer-Policy","origin-when-cross-origin");
        httpResponse.headers().add("X-Frame-Options", "sameorigin");
    }


    /**
     * 解析request中的ip
     * @param request FullHttpRequest
     * @return 用户真是ip
     */
    public static String getRequestIp(FullHttpRequest request) {
        //一些 CDN 会在 CDN 回源请求加 HTTP 头信息，其中可能包含访客的“真实 IP ”。具体要看是哪家 CDN ，比如 Cloudflare 是 CF-Connecting-IP
        String cfIp = request.headers().get("cf-connecting-ip");
        if (!isInnerIp(cfIp) && StringUtils.hasText(cfIp) && !BaseStringConstants.UNKNOWN.equalsIgnoreCase(cfIp)) {
            return cfIp;
        }
        //一些 CDN 会在 CDN 回源请求加 HTTP 头信息，其中可能包含访客的“真实 IP ”。具体要看是哪家 CDN ，比如 Cloudflare 是 CF-Connecting-IP
        String srcIp = request.headers().get("X-Real-IP");
        if (!isInnerIp(srcIp) && StringUtils.hasText(srcIp) && !BaseStringConstants.UNKNOWN.equalsIgnoreCase(srcIp)) {
            return srcIp;
        }

        srcIp = request.headers().get("x-forwarded-for");
        if (StringUtils.hasText(srcIp) && !BaseStringConstants.UNKNOWN.equalsIgnoreCase(srcIp)) {
            String[] ips = srcIp.split(",");
            for (String ip : ips) {
                if (!isInnerIp(ip) && !"unknown".equalsIgnoreCase(ip)) {
                    return ip;
                }
            }
        }

        srcIp = request.headers().get("Proxy-Client-IP");
        if (!isInnerIp(srcIp) && StringUtils.hasText(srcIp) && !BaseStringConstants.UNKNOWN.equalsIgnoreCase(srcIp)) {
            return srcIp;
        }

        srcIp = request.headers().get("WL-Proxy-Client-IP");
        if (!isInnerIp(srcIp) && StringUtils.hasText(srcIp) && !BaseStringConstants.UNKNOWN.equalsIgnoreCase(srcIp)) {
            return srcIp;
        }

        srcIp = request.headers().get("HTTP_CLIENT_IP");
        if (!isInnerIp(srcIp) && StringUtils.hasText(srcIp) && !BaseStringConstants.UNKNOWN.equalsIgnoreCase(srcIp)) {
            return srcIp;
        }

        srcIp = request.headers().get("HTTP_X_FORWARDED_FOR");
        if (!isInnerIp(srcIp) && StringUtils.hasText(srcIp) && !BaseStringConstants.UNKNOWN.equalsIgnoreCase(srcIp)) {
            return srcIp;
        }

        return "";
    }

    public static void main(String[] args) {
        SocketProjectContext context = new SocketProjectContext(new SocketProjectContext.App(MicroServiceConstants.MESSAGE_NETTY_SERVICE), "TEST");
        String sign = JwtUtil.sign(context, BaseMathConstants.ONE_MINUTES_4MILLISECONDS * 60);
        System.out.println(sign);
    }

}
