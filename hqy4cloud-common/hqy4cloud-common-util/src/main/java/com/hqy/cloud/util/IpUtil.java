package com.hqy.cloud.util;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.enums.CountryEnum;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.util.spring.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author qy
 * @date  2021-07-28 19:44
 */
@Slf4j
public class IpUtil {

    static final String VIRTUAL_IP_ENDING = ".1";

    private static MockIpHelper helper = null;

    /**
     * 标准IPv4地址的正则表达式
     */
    private static final Pattern IPV4_REGEX = Pattern
            .compile("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");


    /**
     * 无全0块，标准IPv6地址的正则表达式
     */
    private static final Pattern IPV6_STD_REGEX = Pattern.compile("^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$");

    /**
     * 非边界压缩正则表达式
     */
    private static final Pattern IPV6_COMPRESS_REGEX = Pattern
            .compile("^((?:[0-9A-Fa-f]{1,4}(:[0-9A-Fa-f]{1,4})*)?)::((?:([0-9A-Fa-f]{1,4}:)*[0-9A-Fa-f]{1,4})?)$");

    /**
     * 边界压缩情况正则表达式
     */
    private static final Pattern IPV6_COMPRESS_REGEX_BORDER = Pattern.compile(
            "^(::(?:[0-9A-Fa-f]{1,4})(?::[0-9A-Fa-f]{1,4}){5})|((?:[0-9A-Fa-f]{1,4})(?::[0-9A-Fa-f]{1,4}){5}::)$");

    public static boolean isIP(final String input) {
        return isIPv4Address(input) || isIPv6Address(input);
    }

    /**
     * 判断是否为合法IPv4地址
     * @param input
     * @return
     */
    public static boolean isIPv4Address(final String input) {
        return IPV4_REGEX.matcher(input).matches();
    }

    /**
     * 判断是否为合法IPv6地址
     * @param input
     * @return
     */
    public static boolean isIPv6Address(final String input) {
        int NUM = 0;
        int max = 7;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == ':') {
                NUM++;
            }
        }
        // 合法IPv6地址中不可能有多余7个的冒号(:)
        if (NUM > max) {
            return false;
        }
        if (IPV6_STD_REGEX.matcher(input).matches()) {
            return true;
        }
        // 冒号(:)数量等于7有两种情况：无压缩、边界压缩，所以需要特别进行判断
        if (NUM == max) {
            return IPV6_COMPRESS_REGEX_BORDER.matcher(input).matches();
        }
        // 冒号(:)数量小于七，使用于飞边界压缩的情况
        else {
            return IPV6_COMPRESS_REGEX.matcher(input).matches();
        }
    }

    public static boolean isRequestFromChina(HttpServletRequest request) {
        try {
            CountryEnum cloudFlareCountry = IpUtil.getCloudFlareCountryEnum(request);
            if (cloudFlareCountry == null) {
                return false;
            } else if (cloudFlareCountry.equals(CountryEnum.CN) || cloudFlareCountry.equals(CountryEnum.HK)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getRequestIp(HttpServletRequest request) {
        if (CommonSwitcher.ENABLE_REQUEST_MOCK_IP.isOn()) {
            String debug = request.getQueryString();
            if (StrUtil.isNotBlank(debug) && debug.contains("mock_ip=true")) {
                //模拟生成多个ip
                try {
                    helper = SpringContextHolder.getBean(MockIpHelper.class);
                    String mock_ip = helper.generateIp(request);
                    if (mock_ip != null) {
                        return mock_ip;
                    }
                } catch (Exception ex) {
                    log.error(ex.getMessage());
                }
            }
            if (helper != null) {
                //测试环境调试模式下，获取一个模拟的ip
                //socket.io限流，HTTP限流 和 ab测试场景等模拟压测时使用
                String ip0 = helper.tryGetIp(request);
                if (ip0 != null) {
                    return ip0;
                }
            }
        }

        // 一些 CDN 会在 CDN 回源请求加 HTTP 头信息，其中可能包含访客的“真实 IP ”。具体要看是哪家 CDN ，比如 Cloudflare 是
        // CF-Connecting-IP
        String cfIp = request.getHeader("cf-connecting-ip");
        if (!isInnerIp(cfIp) && StringUtils.hasText(cfIp) && !"unknown".equalsIgnoreCase(cfIp)) {
            return cfIp;
        }

        String srcIp = request.getHeader("X-Real-IP");
        if (!isInnerIp(srcIp) && StringUtils.hasText(srcIp) && !"unknown".equalsIgnoreCase(srcIp)) {
            return srcIp;
        }

        srcIp = request.getHeader("x-forwarded-for");
        if (StringUtils.hasText(srcIp) && !"unknown".equalsIgnoreCase(srcIp)) {
            String[] ips = srcIp.split(",");
            for (String ip : ips) {
                if (!isInnerIp(ip) && !"unknown".equalsIgnoreCase(ip)) {
                    return ip;
                }
            }

        }
        srcIp = request.getHeader("Proxy-Client-IP");
        if (!isInnerIp(srcIp) && StringUtils.hasText(srcIp) && !StringConstants.UNKNOWN.equalsIgnoreCase(srcIp)) {
            return srcIp;
        }

        srcIp = request.getHeader("WL-Proxy-Client-IP");
        if (!isInnerIp(srcIp) && StringUtils.hasText(srcIp) && !StringConstants.UNKNOWN.equalsIgnoreCase(srcIp)) {
            return srcIp;
        }

        srcIp = request.getHeader("HTTP_CLIENT_IP");
        if (!isInnerIp(srcIp) && StringUtils.hasText(srcIp) && !StringConstants.UNKNOWN.equalsIgnoreCase(srcIp)) {
            return srcIp;
        }

        srcIp = request.getHeader("HTTP_X_FORWARDED_FOR");
        if (!isInnerIp(srcIp) && StringUtils.hasText(srcIp) && !StringConstants.UNKNOWN.equalsIgnoreCase(srcIp)) {
            return srcIp;
        }
        return request.getRemoteAddr();
    }



    public static String getRequestIp(ServerHttpRequest request) {
        // 一些 CDN 会在 CDN 回源请求加 HTTP 头信息，其中可能包含访客的“真实 IP ”。具体要看是哪家 CDN ，比如 Cloudflare 是
        HttpHeaders headers = request.getHeaders();
        List<String> list = headers.get("cf-connecting-ip");
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return request.getRemoteAddress().getHostString();
    }

    /**
     * 获取本地主机地址
     * @return
     */
    public static String getHostAddress() {
        try {
            InetAddress candidateAddress = null;
            // 遍历所有的网络接口
            for (Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
                NetworkInterface iface = ifaces.nextElement();
                // 在所有的接口下再遍历IP
                for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                    InetAddress inetAddr = inetAddrs.nextElement();
                    // 排除loopback类型地址
                    if (!inetAddr.isLoopbackAddress()) {
                        if (inetAddr.isSiteLocalAddress()) {
                            return inetAddr.getHostAddress();
                        } else if (candidateAddress == null) {
                            candidateAddress = inetAddr;
                        }
                    }
                }
            }
            if (candidateAddress != null && !candidateAddress.getHostAddress().contains(":")) {
                return candidateAddress.getHostAddress();
            }
            // 如果没有发现 non-loopback地址.只能用最次选的方案
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            return jdkSuppliedAddress.getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static boolean isInnerIp(String ip) {
        return StringConstants.INNER_IP.equals(ip);
    }

    /**
     * Cloudflare ：根据http请求获取来源 国家枚举
     * @param request request
     * @return 如果不是Cloudflare的环境，返回null
     */
    public static CountryEnum getCloudFlareCountryEnum(HttpServletRequest request) {
        String name = getCloudFlareCountry(request);
        if (name != null && !"".equals(name)) {
            try {
                return CountryEnum.valueOf(name);
            } catch (Exception ex) {
                log.warn("getCloudFlareCountryEnum: name={},USE default:YU", name);
                log.warn(ex.getMessage());
                return CountryEnum.DEFAULT_EMPTY;
            }
        }
        return null;
    }

    /**
     * Cloudflare ：根据http请求获取来源国家代码简码： 例如中国CN
     * @param request request
     * @return 如果不是Cloudflare的环境，返回null
     */
    public static String getCloudFlareCountry(HttpServletRequest request) {
        return request.getHeader("cf-ipcountry");
    }

    /**
     *
     * @param hostName hostName
     * @return ip address or hostName if UnknownHostException
     */
    public static String getIpByHost(String hostName) {
        try {
            return InetAddress.getByName(hostName).getHostAddress();
        } catch (UnknownHostException e) {
            return hostName;
        }
    }


}
