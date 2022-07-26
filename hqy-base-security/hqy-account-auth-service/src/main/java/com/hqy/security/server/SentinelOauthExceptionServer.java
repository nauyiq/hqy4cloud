package com.hqy.security.server;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.result.CommonResultCode;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/15 14:23
 */
@Slf4j
public class SentinelOauthExceptionServer {

    public static class Oauth2MeInterfaceHandler {
        // 要求：
        // 1 当前方法的返回值和参数要跟原方法一致
        // 2 参数列表的最后，允许添加一个BlockException参数，用来接收原方法中发生的Sentinel异常
        // 3 需要用static修饰
        public static DataResponse blockHandler(HttpServletRequest request, BlockException e) {
            log.error(e.getMessage());
            return CommonResultCode.dataResponse(CommonResultCode.INTERFACE_LIMITED);
        }

        // 要求：
        // 1 当前方法的返回值和参数要跟原方法一致
        // 2 参数列表的最后，允许添加一个Throwable参数，用来接收原方法中发生的异常
        // 3 需要用static修饰
        public static DataResponse fallbackHandler(HttpServletRequest request, Throwable e) {
            log.error(e.getMessage(), e);
            return CommonResultCode.dataResponse(CommonResultCode.INVALID_DATA);
        }

    }


}
