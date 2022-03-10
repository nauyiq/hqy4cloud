package com.hqy.fundation.common;

/**
 * @author qy
 * @date  2021-08-03 10:08
 */
public interface HttpRequestInfo {

    /**
     * 获取uri
     * @return
     */
    String getUri();

    /**
     * 获取请求的url
     * @return
     */
    String getRequestUrl();

    /**
     * 获取请求方法
     * @return
     */
    String getMethod() ;

    /**
     * 获取请求IP
     * @return
     */
    String getRequestIp() ;

    /**
     * 获取ip国家
     * @return
     */
    String getIpCountry();

    /**
     * 获取相应的请求头值
     * @param header
     * @return
     */
    String getHeader(String header);

    /**
     * 获取请求参数
     * @return
     */
    String getRequestParams();

}
