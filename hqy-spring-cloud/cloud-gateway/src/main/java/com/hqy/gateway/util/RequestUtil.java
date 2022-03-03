package com.hqy.gateway.util;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.LRUCache;
import com.hqy.fundation.common.base.lang.BaseStringConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Flux;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 请求工具类
 * @author qiyuan.hong
 * @date  2021-07-30 15:02
 */
@Slf4j
public class RequestUtil {


    /**
     * 本地默认IPV4 ip
     */
    private static final String IP_LOCAL = "127.0.0.1";

    /**
     * 本地默认IPV6 ip
     */
    private static final String IPV6_LOCAL = "0:0:0:0:0:0:0:1";

    /**
     * ip长度最大值
     */
    private static final int IP_LEN = 15;

    /**
     * 空格,换行和制表符 正则
     */
    private static final String PATTERN_TAP_SPACE = "\\s*|\t|\r|\n";


    /**
     * 获取用户真实IP地址，不直接使用request.getRemoteAddr()的原因是有可能用户使用了代理软件方式避免真实IP地址,
     * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，究竟哪个才是真正的用户端的真实IP呢？
     * 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串。
     * @param request ServerHttpRequest 对象
     */
    public static String getIpAddress(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        String ipAddress = headers.getFirst(BaseStringConstants.X_FORWARDED_FOR);

        //如果请求头x-forwarded-for 没有值则取Proxy-Client-IP
        if (ipAddress == null || ipAddress.length() == 0 || BaseStringConstants.UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = headers.getFirst(BaseStringConstants.PROXY_CLIENT_IP);
        }
        //如果请求头x-forwarded-for 没有值则取WL-Proxy-Client-IP
        if (ipAddress == null || ipAddress.length() == 0 || BaseStringConstants.UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = headers.getFirst(BaseStringConstants.WL_PROXY_CLIENT_IP);
        }

        if (ipAddress == null || ipAddress.length() == 0 || BaseStringConstants.UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = Optional.ofNullable(request.getRemoteAddress())
                    .map(address -> address.getAddress().getHostAddress())
                    .orElse("");
            if (IP_LOCAL.equals(ipAddress)|| IPV6_LOCAL.equals(ipAddress)) {
                // 根据网卡取本机配置的IP
                try {
                    InetAddress inet = InetAddress.getLocalHost();
                    ipAddress = inet.getHostAddress();
                } catch (UnknownHostException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }

        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > IP_LEN) {
            int index = ipAddress.indexOf(",");
            if (index > 0) {
                ipAddress = ipAddress.substring(0, index);
            }
        }
        return ipAddress;
    }


    /**
     * 读取被WrapperRequestGlobalFilter修饰的body内容
     * @param serverHttpRequest
     * @return
     */
    public static String resolveBodyFromRequest(ServerHttpRequest serverHttpRequest) {

        Flux<DataBuffer> body = serverHttpRequest.getBody();
        StringBuilder sb = new StringBuilder();

        body.subscribe(buffer -> {
            byte[] bytes = new byte[buffer.readableByteCount()];
            buffer.read(bytes);
            String bodyString = new String(bytes, StandardCharsets.UTF_8);
            sb.append(bodyString);
        });

        return formatStr(sb.toString());
    }


    /**
     * 去掉空格,换行和制表符
     * @param str
     * @return
     */
    public static String formatStr(String str){
        if (str != null && str.length() > 0) {
            Pattern p = Pattern.compile(PATTERN_TAP_SPACE);
            Matcher m = p.matcher(str);
            return m.replaceAll("");
        }
        return str;
    }


    /**
     * 是否是静态资源或者html
     * @param url
     * @return
     */
    public static boolean isStaticResourceOrHtml(String url) {
        String urlTempString = url.toLowerCase();
        if (url.contains(BaseStringConstants.QUESTION_MARK)) {
            urlTempString = urlTempString.substring(0, urlTempString.indexOf(BaseStringConstants.QUESTION_MARK));
        }
        if (isStaticResource(urlTempString)) {
            return true;
        } else {
            return urlTempString.endsWith(".html") || urlTempString.endsWith(".htm");
        }
    }

    private static final LRUCache<String, Boolean> CACHE =
            CacheUtil.newLRUCache(1024, 60 * 1000 * 10);


    /**
     * 静态资源list
     */
    private static final List<String> STATIC_STRING_LIST = new ArrayList<>();
    static {
        STATIC_STRING_LIST.add(".js");
        STATIC_STRING_LIST.add(".css");
        STATIC_STRING_LIST.add(".ttf");
        STATIC_STRING_LIST.add(".map");
        STATIC_STRING_LIST.add(".png");
        STATIC_STRING_LIST.add(".jpg");
        STATIC_STRING_LIST.add(".jpeg");
        STATIC_STRING_LIST.add(".gif");
        STATIC_STRING_LIST.add(".woff");
        STATIC_STRING_LIST.add(".otf");
        STATIC_STRING_LIST.add("/favicon.ico");
        STATIC_STRING_LIST.add("/resources/");
        STATIC_STRING_LIST.add("/UploadFiles/");
        STATIC_STRING_LIST.add("/files/");
        STATIC_STRING_LIST.add("/csp-report");
        STATIC_STRING_LIST.add("/fonts/");
    }


    /**
     ** 是否是静态资源请求
     * @param url
     * @return true 表示是静态或者类似静态的资源
     */
    public static boolean isStaticResource(String url) {
        String urlTempString = url.toLowerCase();

        if (url.contains(BaseStringConstants.QUESTION_MARK)) {
            urlTempString = urlTempString.substring(0, urlTempString.indexOf(BaseStringConstants.QUESTION_MARK));
        }
        final String key = "isStaticResource.".concat(urlTempString);

        Boolean flag = CACHE.get(key);
        if (flag == null) {
            try {
                if (!urlTempString.startsWith(BaseStringConstants.INCLINED_ROD)) {
                    // 如果不像是静态资源请求.....
                    URL netUrl = new URL(urlTempString);
                    urlTempString = netUrl.getPath();
                }
            } catch (MalformedURLException e) {
                log.warn("MalformedURLException: {}, {}", e.getMessage(), url);
            }

            final String uString = urlTempString;
            boolean match = STATIC_STRING_LIST.stream()
                    .anyMatch(stPattern -> StringUtils.containsIgnoreCase(uString, stPattern));
            CACHE.put(key, match);
            return match;
        } else {
            return flag;
        }
    }


}
