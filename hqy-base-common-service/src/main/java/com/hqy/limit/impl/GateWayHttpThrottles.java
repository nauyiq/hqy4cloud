package com.hqy.limit.impl;

import com.hqy.dto.LimitResult;
import com.hqy.limit.HttpThrottles;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

/**
 * Http限流器，内部实现了系统忙或者客户端频繁访问时，判定要否限流的功能。也能识别出基本的hack或者数据采集，继而判定要限制访问。<br>
 * 核心实现 依赖google 的CacheBuilder
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-07-27 19:58
 */
@Slf4j
public class GateWayHttpThrottles implements HttpThrottles {


    @Override
    public LimitResult limitValue(HttpServletRequest request) {
        return null;
    }
}
