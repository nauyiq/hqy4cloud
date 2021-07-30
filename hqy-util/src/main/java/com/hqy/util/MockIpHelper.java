package com.hqy.util;

import javax.servlet.http.HttpServletRequest;

/**
 * 测试环境调试模式下，获取一个模拟的ip
 * @project: hqy-parent-all
 * @create 2021-07-28 19:44
 */
public interface MockIpHelper {

    static final String  PREFIX = "GEN-";
    
    /**
     ** 初始化一个调试ip
     * @param request
     * @return
     */
    String generateIp(HttpServletRequest request);

    /**
     * 测试环境调试模式下，获取一个模拟的ip
     * @param request
     * @return 如果不是模拟调试模式，返回null
     */
    String tryGetIp(HttpServletRequest request);
    


}
