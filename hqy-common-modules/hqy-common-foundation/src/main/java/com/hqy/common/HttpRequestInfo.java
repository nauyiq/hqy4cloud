package com.hqy.common;

import java.util.Map;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-03 10:08
 */
public interface HttpRequestInfo {

    String getUri();

    String getRequestUrl();

    String getMethod() ;

    String getRequestIp() ;

    String getIpCountry();

    String getHeader(String header);

    Map<String, String> getRequestParams();

}
