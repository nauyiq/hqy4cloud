package com.hqy.cloud.common.request;

/**
 * @author qy
 * @date  2021-08-03 10:08
 */
public interface HttpRequestInfo {

    /**
     * 获取uri
     * @return request uri
     */
    String getUri();

    /**
     * 获取请求的url
     * @return request url
     */
    String getRequestUrl();

    /**
     * 获取请求方法
     * @return request method
     */
    String getMethod() ;

    /**
     * 获取请求IP
     * @return request ip
     */
    String getRequestIp() ;

    /**
     * 获取ip国家
     * @return request country.
     */
    String getIpCountry();

    /**
     * 获取相应的请求头值
     * @param header header key.
     * @return       header value.
     */
    String getHeader(String header);

    /**
     * 获取请求参数
     * @return get request params.
     */
    String getRequestParams();

    /**
     * 获取请求中的body数据
     * @return get request body.
     */
    String getRequestBody();

}
