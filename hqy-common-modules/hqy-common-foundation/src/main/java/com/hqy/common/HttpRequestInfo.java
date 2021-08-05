package com.hqy.common;

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

    String getRequestParams();

}
