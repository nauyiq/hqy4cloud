package com.hqy.limit;

import com.hqy.dto.LimitResult;

import javax.servlet.http.HttpServletRequest;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-02 20:27
 */
public interface HttpThrottles {

    /**
     * 是否允许本次客户端的请求？
     * 同一ip：每秒允许同一ip请求数. 默认8
     * 基于servlet的httpRequest节流
     * @param request
     * @return
     */
    LimitResult limitValue(HttpServletRequest request);

}
